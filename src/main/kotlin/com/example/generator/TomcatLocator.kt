package com.example.generator

import java.io.File

/**
 * 自动探测本机 Tomcat 安装目录。
 *
 * 为什么需要它：pom.xml 里的 tomcat.home 如果写死成某一台机器的路径，
 * 换电脑就会指向不存在的目录。所以在生成项目时先探测一次，
 * 探测不到再留给用户手动指定。
 *
 * 探测顺序（先找到先用）：
 *   1. 环境变量 CATALINA_HOME / CATALINA_BASE
 *   2. 常见安装位置（Apache Software Foundation 目录、各盘根目录、/opt、/usr/local 等）
 *   3. 用户主目录下的 tomcat*
 *
 * 判定标准：目录下同时存在 bin、conf、lib、webapps 四个子目录。
 * 纯文件系统检查，不引用任何 IntelliJ 类。
 *
 * 注意「是 Tomcat」和「cargo 能启动它」是两回事，见 [isCargoReady]。
 */
object TomcatLocator {

    /** 找不到时写进 pom 的占位值，让人一眼看出要改这里 */
    const val PLACEHOLDER = "请把这里改成你的Tomcat安装目录"

    /**
     * 优先返回 cargo 能真正启动的那个 Tomcat：
     * 一台机器上可能装了不止一个（比如官网安装包装一个、自己解压又放一个），
     * 其中有的被精简过 webapps，cargo 的 standalone 配置复制不到东西就会启动失败。
     * 都不可用时退而返回一个「看起来是 Tomcat」的目录，至少路径是对的，
     * 体检动作会把具体问题指出来。
     */
    fun findTomcat(): File? = findAllTomcats().firstOrNull()

    /** 本机探测到的全部 Tomcat 目录，cargo 能启动的排在前面 */
    fun findAllTomcats(): List<File> =
        candidates().filter { isTomcatHome(it) }.sortedByDescending { isCargoReady(it) }

    /** 目录下是否有 Tomcat 该有的四个子目录 */
    fun isTomcatHome(dir: File): Boolean =
        dir.isDirectory && SUB_DIRS.all { File(dir, it).isDirectory }

    /**
     * cargo 用 type=standalone 启动时，会把源 Tomcat 的 manager 与 host-manager
     * 两个应用复制到它自己那套配置下，缺任何一个都会直接抛：
     *   ContainerException: Source [.../webapps/host-manager] is not a directory
     * 所以这两个目录可作为「cargo 能用」的判据。
     */
    fun isCargoReady(dir: File): Boolean =
        isTomcatHome(dir) && CARGO_REQUIRED_WEBAPPS.all { File(dir, "webapps/$it").isDirectory }

    /** cargo standalone 配置必需的 webapps 子目录 */
    val CARGO_REQUIRED_WEBAPPS = listOf("manager", "host-manager")

    private fun candidates(): List<File> {
        val result = LinkedHashSet<File>()

        // 1. 环境变量
        for (key in listOf("CATALINA_HOME", "CATALINA_BASE")) {
            System.getenv(key)?.trim()?.takeIf { it.isNotEmpty() }?.let { result += File(it) }
        }

        // 2. 常见父目录下的一级子目录
        val parents = listOf(
            "C:/Program Files/Apache Software Foundation",
            "C:/Program Files (x86)/Apache Software Foundation",
            "C:/", "D:/", "E:/", "F:/",
            "/opt", "/usr/local", "/usr/share", "/usr/local/share",
        )
        for (p in parents) {
            val parent = File(p)
            if (!parent.isDirectory) continue
            // 父目录本身就是 tomcat（例如 /opt/tomcat）
            if (parent.name.contains("tomcat", ignoreCase = true)) result += parent
            parent.listFiles()?.forEach { child ->
                if (child.isDirectory && child.name.contains("tomcat", ignoreCase = true)) {
                    result += child
                }
            }
        }

        // 3. 用户主目录下的 tomcat*
        File(System.getProperty("user.home")).listFiles()?.forEach { child ->
            if (child.isDirectory && child.name.contains("tomcat", ignoreCase = true)) {
                result += child
            }
        }

        return result.toList()
    }

    private val SUB_DIRS = listOf("bin", "conf", "lib", "webapps")
}
