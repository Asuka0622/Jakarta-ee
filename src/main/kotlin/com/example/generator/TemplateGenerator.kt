package com.example.generator

import java.nio.file.Files
import java.nio.file.Path

/**
 * 把插件内置的模板文件写到目标项目目录。
 *
 * 刻意不引用任何 IntelliJ 平台的类：
 *   1. 它做的纯粹是文件拷贝，没有理由依赖平台
 *   2. 这样能脱离 IDE 直接跑，便于端到端验证（生成 → 打包 → 启动 → 请求）
 */
object TemplateGenerator {

    /** 模板资源在插件 jar 中的前缀 */
    private const val TEMPLATE_ROOT = "/templates/"

    /**
     * pom.xml 里 tomcat.home 的占位符，生成时替换成本机探测到的 Tomcat 路径。
     * 探测不到就替换成 [TomcatLocator.PLACEHOLDER]（一句提示文字），
     * 让用户一眼看出要改这里，体检动作也会指出并带着改。
     */
    private const val TOMCAT_HOME_TOKEN = "@@TOMCAT_HOME@@"

    /** 模板里写死的默认应用名；生成时整体替换成用户在向导里填的名字 */
    const val DEFAULT_APP_NAME = "demo1"

    /**
     * 要生成的文件清单，路径相对于项目根目录，
     * 与 resources/templates 下的目录结构完全一致（同名同层级）。
     *
     * 想给模板加文件时，两处都要加：
     *   1) 在 resources/templates/ 下新建同名文件
     *   2) 把相对路径加到本列表
     */
    val TEMPLATE_FILES = listOf(
        "pom.xml",
        "README.md",
        "start-tomcat.bat",
        ".run/StartTomcat.run.xml",
        "src/main/webapp/index.html",
        "src/main/webapp/WEB-INF/web.xml",
        "src/main/java/com/example/demo/HelloServlet.java",
        "src/main/java/com/example/demo/LifecycleServlet.java",
        "src/main/java/com/example/demo/ParamsServlet.java",
        "src/main/java/com/example/demo/ForwardRedirectServlet.java",
    )

    /**
     * 把插件内置的模板文件逐个拷贝到目标项目目录。
     *
     * 模板都是 UTF-8 纯文本，统一按「读成字符串 → 替换 → 写回 UTF-8」处理。
     * 这种往返对合法 UTF-8 是无损的，中文注释不会被二次编码弄乱。
     *
     * @param appName 应用名，见 [sanitizeAppName]；决定项目名与访问路径
     */
    fun generateFiles(basePath: Path, appName: String = DEFAULT_APP_NAME) {
        val app = sanitizeAppName(appName)
        val tomcatHome = TomcatLocator.findTomcat()?.path?.replace('\\', '/')
            ?: TomcatLocator.PLACEHOLDER

        for (relative in TEMPLATE_FILES) {
            val resource = "$TEMPLATE_ROOT$relative"
            val bytes = TemplateGenerator::class.java
                .getResourceAsStream(resource)
                ?.use { it.readBytes() }
                ?: error("插件内置模板缺失：$resource")

            var text = String(bytes, Charsets.UTF_8)
            if (app != DEFAULT_APP_NAME) {
                // 模板正文里凡是提到应用名的地方都写作 demo1（例如 demo1_war_exploded、
                // com.example:demo1、target/demo1.war），整体替换即可，
                // artifactId、war 文件名、上下文路径和文档里的地址会一起改过来
                text = text.replace(DEFAULT_APP_NAME, app)
            }
            if (relative == "pom.xml") {
                text = text.replace(TOMCAT_HOME_TOKEN, tomcatHome)
            }

            val target = basePath.resolve(relative)
            // 目标可能位于多层子目录（如 src/main/java/...），父目录需先建好
            target.parent?.let { Files.createDirectories(it) }
            Files.write(target, text.toByteArray(Charsets.UTF_8))
        }
    }

    /**
     * 规范化用户在向导里填的应用名。
     *
     * 这个名字会被用作 Maven 的 artifactId、war 文件名、部署后的上下文路径，
     * 还会写进 start-tomcat.bat 和 pom 的注释里，出现空格、中文、斜杠等字符
     * 会让 Maven 构建失败或让 bat 跑不起来，所以只保留字母、数字、- 和 _；
     * 清完为空、或首字符不是字母（Maven 不允许数字开头）就退回默认名。
     */
    fun sanitizeAppName(raw: String?): String {
        val cleaned = raw.orEmpty().trim().replace(Regex("[^A-Za-z0-9_-]"), "")
        return if (cleaned.isEmpty() || !cleaned[0].isLetter()) DEFAULT_APP_NAME else cleaned
    }
}
