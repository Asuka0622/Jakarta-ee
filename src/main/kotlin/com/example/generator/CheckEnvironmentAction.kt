package com.example.generator

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import java.io.File

/**
 * 「检查 Tomcat / cargo 运行环境」动作。
 *
 * 检查内容都在 [EnvironmentChecker] 里（纯文件检查，不起进程）。
 * 如果发现 tomcat.home 不可用，会多给一个入口：直接选目录并写回 pom.xml，
 * 省掉手工改文件这一步。
 */
class CheckEnvironmentAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val basePath = project?.basePath
        val report = EnvironmentChecker.buildReport(basePath)

        // 只有「打开了项目 + 有 pom + tomcat.home 不可用」时才提供修复入口
        val pom = basePath?.let { File(it, "pom.xml") }
        if (pom != null && pom.isFile) {
            val status = EnvironmentChecker.inspectTomcatHome(pom)
            if (!status.usable) {
                val current = status.value ?: "（pom 里没有这个属性）"
                val choice = Messages.showYesNoDialog(
                    project,
                    "$report\n\n当前 tomcat.home：$current\n\n是否现在选择 Tomcat 安装目录并写回 pom.xml？",
                    "运行环境检查",
                    "选择目录并修复",
                    "仅查看报告",
                    null
                )
                if (choice == Messages.YES) {
                    pickAndFix(project, pom)
                }
                return
            }
        }
        Messages.showMessageDialog(project, report, "运行环境检查", null)
    }

    /** 弹目录选择器，校验后写回 pom.xml */
    private fun pickAndFix(project: Project?, pom: File) {
        val chosen = FileChooser.chooseFile(
            FileChooserDescriptorFactory.createSingleFolderDescriptor(),
            project,
            null
        ) ?: return

        val dir = File(chosen.path)
        if (!TomcatLocator.isTomcatHome(dir)) {
            Messages.showErrorDialog(
                project,
                "这个目录不像 Tomcat 安装目录（下面四个子目录至少要都有）：\n" +
                        "bin / conf / lib / webapps\n\n所选目录：${dir.path}",
                "目录不对"
            )
            return
        }

        val path = dir.path.replace('\\', '/')
        if (PomTomcatHome.update(pom, path)) {
            Messages.showInfoMessage(
                project,
                "已写入 pom.xml：\n\n<tomcat.home>$path</tomcat.home>\n\n" +
                        "现在可以执行：mvn clean package cargo:run",
                "修复完成"
            )
        } else {
            Messages.showErrorDialog(
                project,
                "pom.xml 里没有 <tomcat.home> 元素，无法自动写入。\n" +
                        "请手工在 <properties> 里加上：\n\n<tomcat.home>$path</tomcat.home>",
                "修复失败"
            )
        }
    }
}
