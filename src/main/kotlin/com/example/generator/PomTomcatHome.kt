package com.example.generator

import java.io.File

/**
 * 读写 pom.xml 里的 `<tomcat.home>` 属性。
 *
 * 只做一处文本替换，不用 DOM 重新序列化 —— 那样会把模板里的注释和格式全部打乱。
 * 纯文件读写，不引用任何 IntelliJ 类，方便无头测试。
 */
object PomTomcatHome {

    private val ELEMENT = Regex("<tomcat\\.home>\\s*(.*?)\\s*</tomcat\\.home>")

    /** 读出当前值；元素不存在返回 null */
    fun read(pom: File): String? {
        if (!pom.isFile) return null
        return ELEMENT.find(pom.readText(Charsets.UTF_8))?.groupValues?.get(1)?.trim()
    }

    /**
     * 把值改成 [newPath]。
     * @return true 表示改成功；false 表示 pom 里压根没有这个元素（需要手工加）
     */
    fun update(pom: File, newPath: String): Boolean {
        if (!pom.isFile) return false
        val text = pom.readText(Charsets.UTF_8)
        if (!ELEMENT.containsMatchIn(text)) return false
        val replaced = ELEMENT.replace(text) { "<tomcat.home>$newPath</tomcat.home>" }
        pom.writeText(replaced, Charsets.UTF_8)
        return true
    }
}
