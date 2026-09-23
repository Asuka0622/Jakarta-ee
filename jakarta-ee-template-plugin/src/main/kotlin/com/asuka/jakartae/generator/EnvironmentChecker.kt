package com.asuka.jakartae.generator

import com.example.generator.TomcatLocator
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import java.io.IOException
import java.net.InetSocketAddress
import java.net.ServerSocket
import javax.xml.parsers.DocumentBuilderFactory

/**
 * 运行环境体检。
 *
 * 背景：cargo 插件在运行时才去本地 Maven 仓库解析容器相关的 jar
 * （比如 DefaultJvmLauncher 所在的 cargo-core-api-container），
 * 换一台电脑后如果这些 jar 没下全或下载损坏，
 * 只会在启动 Tomcat 那一刻抛 NoClassDefFoundError，很难看出缺了什么。
 *
 * 这里把要检查的东西集中成一份报告，纯文件检查、不起进程。
 * 刻意不引用任何 IntelliJ 平台的类，这样既能被动作调用，
 * 也能脱离 IDE 单独跑（便于验证报告内容是否准确）。
 */
object EnvironmentChecker {

    /** pom 里没写 cargo.servlet.port 时的默认端口 */
    private const val DEFAULT_PORT = 8080

    /** cargo 运行时需要的 jar */
    private val REQUIRED_CARGO_ARTIFACTS = listOf(
        "cargo-maven3-plugin",
        "cargo-core-api-container",
        "cargo-core-api-generic",
        "cargo-core-api-module",
        "cargo-core-api-util",
        "cargo-core-container-tomcat",
        "cargo-daemon-client",
        "cargo-documentation",
        "cargo-licensed-dtds",
    )

    /** tomcat.home 有问题时给的三条出路 */
    private val TOMCAT_HOWTO = listOf(
        "        三种改法，任选一种：",
        "          1) 改 pom.xml 的 properties：",
        "                 <tomcat.home>本机Tomcat安装目录</tomcat.home>",
        "          2) 不改文件，命令行临时覆盖：",
        "                 mvn clean package cargo:run -Dtomcat.home=D:/你的/tomcat目录",
        "          3) 这台电脑没装 Tomcat：把 pom 里 cargo 插件的 home 换成注释里",
        "             保留的 zipUrlInstaller 写法，让 Cargo 自动下载一个 Tomcat。",
    )

    /**
     * 单独看一眼 tomcat.home（供动作判断要不要弹出「选择目录」入口）。
     * @param usable 目录存在、像个 Tomcat，且 cargo 能拿它启动
     */
    data class TomcatStatus(val value: String?, val usable: Boolean)

    fun inspectTomcatHome(pom: File): TomcatStatus {
        val doc = parse(pom) ?: return TomcatStatus(null, false)
        val value = firstElementText(doc, "tomcat.home")?.trim()
        if (value.isNullOrBlank()) return TomcatStatus(null, false)
        return TomcatStatus(value, TomcatLocator.isCargoReady(File(value)))
    }

    private fun parse(pom: File): Document? = try {
        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pom)
    } catch (_: Throwable) {
        null
    }

    /**
     * 生成体检报告。
     * @param basePath 项目根目录；传 null 表示当前没有打开项目
     */
    fun buildReport(basePath: String?): String {
        val lines = mutableListOf<String>()

        if (basePath.isNullOrBlank()) {
            return "没有打开任何项目。\n请先打开由本插件生成的 Maven 项目，再执行本检查。"
        }
        lines += "项目目录：$basePath"

        // ---------- 1. pom.xml ----------
        val pom = File(basePath, "pom.xml")
        if (!pom.isFile) {
            lines += ""
            lines += "[缺失] 项目根目录没有 pom.xml"
            lines += "本检查只对由「Jakarta EE Web 模板」生成的 Maven 项目有效。"
            return lines.joinToString("\n")
        }
        lines += ""
        lines += "[正常] 找到 pom.xml"

        val doc: Document = try {
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pom)
        } catch (t: Throwable) {
            lines += ""
            lines += "[缺失] pom.xml 解析失败：${t.message}"
            return lines.joinToString("\n")
        }

        // ---------- 2. cargo 插件与版本 ----------
        val cargoVersion = findCargoPluginVersion(doc)
        lines += ""
        if (cargoVersion == null) {
            lines += "[缺失] pom.xml 里没有配置 cargo-maven3-plugin"
            lines += "没有它就无法用一条命令启动 Tomcat；"
            lines += "可以用本插件重新生成一次项目，或参考 README 手工补上。"
            return lines.joinToString("\n")
        }
        lines += "[正常] cargo-maven3-plugin 版本 $cargoVersion"

        // ---------- 3. tomcat.home ----------
        val tomcatHome = firstElementText(doc, "tomcat.home")
        lines += ""
        var tomcatOk = false
        if (tomcatHome.isNullOrBlank()) {
            lines += "[缺失] pom.xml 里没有 tomcat.home 属性"
            lines += TOMCAT_HOWTO
        } else {
            val home = File(tomcatHome.trim())
            val missingDirs = listOf("bin", "conf", "lib", "webapps").filter { !File(home, it).isDirectory }
            if (!home.isDirectory) {
                lines += "[缺失] tomcat.home 指向的目录不存在：${tomcatHome.trim()}"
                lines += "        这个值写在 pom.xml 的 properties 里，是当年生成模板那台机器的路径，"
                lines += "        换电脑后必须改成本机自己的 Tomcat 目录。"
                lines += TOMCAT_HOWTO
            } else if (missingDirs.isNotEmpty()) {
                lines += "[缺失] tomcat.home 不像一个 Tomcat 安装目录：${tomcatHome.trim()}"
                lines += "        缺少子目录：${missingDirs.joinToString(", ")}"
                lines += TOMCAT_HOWTO
            } else if (!TomcatLocator.isCargoReady(home)) {
                // 「是个 Tomcat」不等于「cargo 能启动它」：
                // 有的安装包/精简过的 Tomcat 没有 webapps/manager 与 webapps/host-manager，
                // cargo 的 standalone 配置复制不到，启动时才会抛异常。
                val missingApps = TomcatLocator.CARGO_REQUIRED_WEBAPPS
                    .filter { !File(home, "webapps/$it").isDirectory }
                lines += "[缺失] tomcat.home 缺少 cargo 启动所需的应用：${tomcatHome.trim()}"
                lines += "        缺少：${missingApps.joinToString("、") { "webapps/$it" }}"
                lines += "        cargo 以 standalone 方式启动时会复制这两个应用，缺了会报："
                lines += "            ContainerException: Source [.../webapps/host-manager] is not a directory"
                lines += "        改成本机另一个完整的 Tomcat 目录一般就好了。"
                lines += TOMCAT_HOWTO
            } else {
                lines += "[正常] tomcat.home 可用：${tomcatHome.trim()}"
                tomcatOk = true
            }
            if (!tomcatOk) {
                lines += ""
                lines += alternativeTomcats(tomcatHome.trim())
            }
        }

        // ---------- 4. 启动端口 ----------
        // 端口被占用时 cargo 会抛 ContainerException 然后启动失败，
        // 而 8080 上往往已经跑着另一个 Tomcat（残留进程或 Windows 服务），
        // 浏览器连过去的其实是那个 Tomcat，里面没有本项目，表现就是「启动成功却 404」。
        val port = servletPort(doc)
        lines += ""
        if (isPortFree(port)) {
            lines += "[正常] 端口 $port 空闲"
        } else {
            lines += "[缺失] 端口 $port 已被占用，cargo 启动 Tomcat 会失败"
            lines += "        报错原文："
            lines += "            Port number $port (defined with the property cargo.servlet.port) is in use."
            lines += "        这时浏览器访问 $port 端口，连到的是那个已存在的 Tomcat，"
            lines += "        它里面没有本项目，所以怎么访问都是 404。"
            lines += "        三种处理办法，任选一种："
            lines += "          1) 查出占用端口的进程并结束它："
            lines += "                 netstat -ano | findstr :$port"
            lines += "                 最后一列是 PID，再到任务管理器里结束那个进程"
            lines += "             或一条命令搞定（PowerShell）："
            lines += "                 Stop-Process -Id (Get-NetTCPConnection -LocalPort $port -State Listen).OwningProcess -Force"
            lines += "          2) 占用者是另一个 Tomcat 的 Windows 服务时，以管理员身份："
            lines += "                 Get-Service *Tomcat*"
            lines += "                 Stop-Service <服务名>"
            lines += "          3) 干脆换个端口：把 pom.xml 里 cargo 插件配置中的"
            lines += "                 <cargo.servlet.port>8080</cargo.servlet.port>"
            lines += "             改成 8081（或启动命令后加 -Dcargo.servlet.port=8081），"
            lines += "             启动后访问 http://localhost:8081/demo1_war_exploded/"
        }

        // ---------- 5. 本地 Maven 仓库里的 cargo 依赖 ----------
        val repos = localRepositories()
        lines += ""
        lines += "[检查] cargo 依赖（本地 Maven 仓库）"
        for (repo in repos) {
            lines += "  仓库：$repo"
            if (!repo.isDirectory) {
                lines += "        该仓库目录不存在，跳过"
                continue
            }
            val missing = REQUIRED_CARGO_ARTIFACTS.filter { !cargoJar(repo, it, cargoVersion).isFile }
            if (missing.isEmpty()) {
                lines += "        [正常] ${REQUIRED_CARGO_ARTIFACTS.size} 个 cargo 依赖齐全"
            } else {
                lines += "        [缺失] 缺 ${missing.size} 个："
                for (a in missing) {
                    lines += "               $a-$cargoVersion.jar"
                    if (a == "cargo-core-api-container") {
                        lines += "                 （报 NoClassDefFoundError: DefaultJvmLauncher 就是缺这个）"
                    }
                }
                lines += "        修复："
                lines += "          1) 删除 cargo 缓存：rmdir /s /q \"${File(repo, "org/codehaus/cargo").path}\""
                lines += "          2) 重新下载：mvn -U clean package cargo:run"
                lines += "        若仍失败，多半是镜像缺包：把 settings.xml 的 mirror 临时指向"
                lines += "        https://repo.maven.apache.org/maven2 再试一次。"
            }
        }

        // ---------- 6. 结论 ----------
        lines += ""
        val ok = lines.none { it.contains("[缺失]") }
        if (ok) {
            lines += "结论：环境就绪，执行下面这条即可启动："
            lines += "    mvn clean package cargo:run"
            lines += "启动后访问：http://localhost:8080/demo1_war_exploded/"
        } else {
            lines += "结论：上面标了 [缺失] 的项需要先处理好，否则启动 Tomcat 时会报错。"
        }
        return lines.joinToString("\n")
    }

    /** 在 pom 里找 cargo-maven3-plugin，返回它的 version */
    private fun findCargoPluginVersion(doc: Document): String? {
        val plugins = doc.getElementsByTagName("plugin")
        for (i in 0 until plugins.length) {
            val plugin = plugins.item(i)
            if (plugin !is Element) continue
            if (childText(plugin, "artifactId") == "cargo-maven3-plugin") {
                return childText(plugin, "version") ?: "未知"
            }
        }
        return null
    }

    private fun childText(parent: Element, tag: String): String? {
        val nodes = parent.getElementsByTagName(tag)
        for (i in 0 until nodes.length) {
            val n = nodes.item(i)
            if (n.parentNode === parent) return n.textContent?.trim()
        }
        return null
    }

    /**
     * tomcat.home 不可用时，列出本机其他可用的 Tomcat。
     * 这台机器上可能装了不止一个 Tomcat，自动探测选中的未必是能用的那个，
     * 把候选直接写出来，用户照着换一行属性就行，不用自己去翻磁盘。
     */
    private fun alternativeTomcats(current: String): String {
        val normalized = current.replace('\\', '/')
        val others = TomcatLocator.findAllTomcats()
            .filter { it.path.replace('\\', '/') != normalized }
        if (others.isEmpty()) {
            return "        本机没有探测到其他 Tomcat。"
        }
        return buildString {
            append("        本机探测到这些 Tomcat，可挑一个能用的填进 tomcat.home：")
            for (t in others) {
                val note = if (TomcatLocator.isCargoReady(t)) {
                    "可用"
                } else {
                    "不可用（缺 webapps/manager 或 webapps/host-manager）"
                }
                append("\n          ").append(t.path.replace('\\', '/')).append("  ").append(note)
            }
        }
    }

    /**
     * cargo 的启动端口：pom 里写了就用写的，没写就是 Tomcat 默认的 8080。
     */
    private fun servletPort(doc: Document): Int =
        firstElementText(doc, "cargo.servlet.port")?.trim()?.toIntOrNull() ?: DEFAULT_PORT

    /**
     * 端口是不是空的。
     * 做法是「自己也占一下」：cargo 启动时绑的是同一个端口，绑不上就会
     * 抛 ContainerException 然后启动失败，所以这里能绑上就等于 cargo 能起来。
     *
     * 必须先关掉 reuseAddress：Windows 上带着 SO_REUSEADDR 去绑一个已被监听的
     * 端口居然会成功，那样就会把「被占用」误判成「空闲」。
     */
    fun isPortFree(port: Int): Boolean = try {
        ServerSocket().use {
            it.reuseAddress = false
            it.bind(InetSocketAddress(port))
        }
        true
    } catch (_: IOException) {
        false
    }

    private fun firstElementText(doc: Document, tag: String): String? {
        val nodes = doc.getElementsByTagName(tag)
        if (nodes.length == 0) return null
        return nodes.item(0).textContent
    }

    /** 默认的 ~/.m2/repository，外加 settings.xml 里 localRepository 指定的仓库 */
    private fun localRepositories(): List<File> {
        val result = LinkedHashSet<File>()
        val userHome = File(System.getProperty("user.home"))
        result += File(userHome, ".m2/repository")

        val settings = File(userHome, ".m2/settings.xml")
        if (settings.isFile) {
            try {
                val text = settings.readText(Charsets.UTF_8)
                val m = Regex("<localRepository>\\s*(.*?)\\s*</localRepository>").find(text)
                val path = m?.groupValues?.get(1)
                if (!path.isNullOrBlank()) result += File(path)
            } catch (_: Throwable) {
                // 读不到 settings.xml 就只用默认仓库，不影响主流程
            }
        }
        return result.toList()
    }

    private fun cargoJar(repo: File, artifactId: String, version: String): File =
        File(repo, "org/codehaus/cargo/$artifactId/$version/$artifactId-$version.jar")
}
