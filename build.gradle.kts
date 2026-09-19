plugins {
    // 必须放在最前面声明，IntelliJ 官方 Gradle 插件
    id("org.jetbrains.intellij") version "1.17.4"
    // IC 2026.1 的平台模块用 Kotlin 2.3.0 编译，旧版 Kotlin(1.9) 读不了其元数据
    // 必须用 Kotlin 2.3.x 才能解析 com.intellij.platform.* 依赖
    kotlin("jvm") version "2.3.0"
}

group = "com.example"
version = "1.3.2"

repositories {
    mavenCentral()
}

// 强制 UTF-8，避免 Windows 下中文/特殊字符乱码
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
}

intellij {
    pluginName.set("jakartaee-project-generator")

    // 目标是社区版（IC）且"任意 2026 版本都可用"。
    // 因此对编译到 2026 系列里最旧的 2026.1（build 261），避免用到更新版专属 API，
    // 这样运行在 2026.1 / 2026.2 / 2026.3 上都不会因引用了更低版本缺失的类而报错。
    version.set("2026.1")
    // ✅ IC = Community 社区版 SDK，最重要！禁止用 IU（旗舰版）
    type.set("IC")
    // 不声明 plugins，让完整 IC SDK（含 com.intellij.platform.* 等模块）进入编译类路径，
    // 否则 com.intellij.platform.DirectoryProjectGenerator 会解析不到
    // plugins.set(listOf("com.intellij.java", "maven"))
}

tasks {
    patchPluginXml {
        // 2026.x 系列 build：2026.1=261 / 2026.2=262 / 2026.3=263
        sinceBuild.set("261")
        untilBuild.set("263.*")

        pluginDescription.set("""
            <p>一键生成 Jakarta-EE Maven Web 教学模板</p>
            <p>在「新建项目」面板多出 <b>Jakarta EE</b> 选项，自动生成带完整中文注释的项目骨架：
               pom.xml、web.xml、index.html、README.md，以及 4 个示例 Servlet
               （基础用法 / 生命周期 / 请求参数与中文编码 / 转发与重定向）。</p>
            <p>社区版（IC）即可用，仅生成磁盘文件，无旗舰版 EE 语法提示。</p>
        """.trimIndent())
    }

    // ./gradlew buildPlugin 产出 zip 在 build/distributions/ 下
    buildPlugin {
        // 打包插件，供本地磁盘安装
    }

    // 本插件没有任何设置项/action，buildSearchableOptions 会误报失败并阻塞打包，直接关闭
    buildSearchableOptions {
        enabled = false
    }
}