package com.asuka.jakartae.generator

import com.example.generator.TomcatLocator
import java.nio.file.Files
import java.nio.file.Path

/**
 * JSP 教学模板项目生成器。
 *
 * 生成以 JSP 为核心的 Java Web 项目，包含 9 个循序渐进的 JSP 示例页面，
 * 覆盖基础语法、内置对象、指令、表单、作用域、JavaBean、JSTL、EL、页面包含。
 *
 * 刻意不引用任何 IntelliJ 平台的类，便于脱离 IDE 测试。
 */
object JspTemplateGenerator {

    /** 模板资源在插件 jar 中的前缀 */
    private const val TEMPLATE_ROOT = "/templates-jsp/"

    /** pom.xml 里 tomcat.home 的占位符 */
    private const val TOMCAT_HOME_TOKEN = "@@TOMCAT_HOME@@"

    /** 模板里写死的默认应用名；生成时整体替换成用户在向导里填的名字 */
    const val DEFAULT_APP_NAME = "demo1"

    /**
     * 要生成的文件清单，路径相对于项目根目录，
     * 与 resources/templates-jsp 下的目录结构完全一致。
     */
    val TEMPLATE_FILES = listOf(
        "pom.xml",
        "README.md",
        "start-tomcat.bat",
        ".run/StartTomcat.run.xml",
        "src/main/webapp/index.jsp",
        "src/main/webapp/01-hello.jsp",
        "src/main/webapp/02-objects.jsp",
        "src/main/webapp/03-directive.jsp",
        "src/main/webapp/04-form.jsp",
        "src/main/webapp/05-counter.jsp",
        "src/main/webapp/06-javabean.jsp",
        "src/main/webapp/07-jstl.jsp",
        "src/main/webapp/08-el.jsp",
        "src/main/webapp/09-include.jsp",
        "src/main/webapp/common/header.jspf",
        "src/main/webapp/common/footer.jspf",
        "src/main/webapp/WEB-INF/web.xml",
        "src/main/java/com/example/bean/User.java",
    )

    /**
     * 把插件内置的 JSP 模板逐个拷贝到目标项目目录。
     *
     * @param appName 应用名，决定 artifactId、war 文件名、上下文路径
     */
    fun generateFiles(basePath: Path, appName: String = DEFAULT_APP_NAME) {
        val app = sanitizeAppName(appName)
        val tomcatHome = TomcatLocator.findTomcat()?.path?.replace('\\', '/')
            ?: TomcatLocator.PLACEHOLDER

        for (relative in TEMPLATE_FILES) {
            val resource = "$TEMPLATE_ROOT$relative"
            val bytes = JspTemplateGenerator::class.java
                .getResourceAsStream(resource)
                ?.use { it.readBytes() }
                ?: error("插件内置模板缺失：$resource")

            var text = String(bytes, Charsets.UTF_8)
            if (app != DEFAULT_APP_NAME) {
                // 模板里所有 demo1 都替换成用户的应用名
                text = text.replace(DEFAULT_APP_NAME, app)
            }
            if (relative == "pom.xml") {
                text = text.replace(TOMCAT_HOME_TOKEN, tomcatHome)
            }

            val target = basePath.resolve(relative)
            target.parent?.let { Files.createDirectories(it) }
            Files.write(target, text.toByteArray(Charsets.UTF_8))
        }
    }

    /**
     * 规范化应用名：只保留字母、数字、- 和 _，首字符必须是字母。
     */
    fun sanitizeAppName(raw: String?): String {
        val cleaned = raw.orEmpty().trim().replace(Regex("[^A-Za-z0-9_-]"), "")
        return if (cleaned.isEmpty() || !cleaned[0].isLetter()) DEFAULT_APP_NAME else cleaned
    }
}
