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
     * 默认按字节读写（不做字符串转换），这样 UTF-8 的中文注释不会被二次编码弄乱。
     * 只有 pom.xml 需要做一次文本替换（填 Tomcat 路径），它本身是 UTF-8，安全。
     */
    fun generateFiles(basePath: Path) {
        val tomcatHome = TomcatLocator.findTomcat()?.path?.replace('\\', '/')
            ?: TomcatLocator.PLACEHOLDER

        for (relative in TEMPLATE_FILES) {
            val resource = "$TEMPLATE_ROOT$relative"
            val bytes = TemplateGenerator::class.java
                .getResourceAsStream(resource)
                ?.use { it.readBytes() }
                ?: error("插件内置模板缺失：$resource")

            val content = if (relative == "pom.xml") {
                String(bytes, Charsets.UTF_8).replace(TOMCAT_HOME_TOKEN, tomcatHome).toByteArray(Charsets.UTF_8)
            } else {
                bytes
            }

            val target = basePath.resolve(relative)
            // 目标可能位于多层子目录（如 src/main/java/...），父目录需先建好
            target.parent?.let { Files.createDirectories(it) }
            Files.write(target, content)
        }
    }
}
