package com.asuka.jakartae.generator

import java.nio.file.Files
import java.nio.file.Path

/**
 * 静态 HTML 项目生成器。
 *
 * 只生成纯前端文件（HTML / CSS / JS），不需要 Java、Maven、Tomcat，
 * 双击 index.html 或用任意静态服务器就能打开。
 *
 * 刻意不引用任何 IntelliJ 平台的类：
 *   1. 它做的纯粹是文件拷贝，没有理由依赖平台
 *   2. 这样能脱离 IDE 直接跑，便于端到端验证
 */
object StaticHtmlGenerator {

    /** 模板资源在插件 jar 中的前缀 */
    private const val TEMPLATE_ROOT = "/templates-static/"

    /**
     * 要生成的文件清单，路径相对于项目根目录，
     * 与 resources/templates-static 下的目录结构完全一致。
     *
     * 想给模板加文件时，两处都要加：
     *   1) 在 resources/templates-static/ 下新建同名文件
     *   2) 把相对路径加到本列表
     */
    val TEMPLATE_FILES = listOf(
        "index.html",
        "css/style.css",
        "js/script.js",
        "README.md",
    )

    /**
     * 把插件内置的静态模板文件逐个拷贝到目标项目目录。
     *
     * 模板都是 UTF-8 纯文本，统一按「读字节 → 直接写回」处理，
     * 不做任何字符串替换，避免意外改动用户模板内容。
     */
    fun generateFiles(basePath: Path) {
        for (relative in TEMPLATE_FILES) {
            val resource = "$TEMPLATE_ROOT$relative"
            val bytes = StaticHtmlGenerator::class.java
                .getResourceAsStream(resource)
                ?.use { it.readBytes() }
                ?: error("插件内置模板缺失：$resource")

            val target = basePath.resolve(relative)
            // 目标可能位于多层子目录（如 css/、js/），父目录需先建好
            target.parent?.let { Files.createDirectories(it) }
            Files.write(target, bytes)
        }
    }
}
