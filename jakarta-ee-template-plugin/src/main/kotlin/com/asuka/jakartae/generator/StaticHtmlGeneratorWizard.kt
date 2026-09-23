package com.asuka.jakartae.generator

import com.intellij.icons.AllIcons
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.GeneratorNewProjectWizard
import com.intellij.ide.wizard.NewProjectWizardBaseStep
import com.intellij.ide.wizard.NewProjectWizardChainStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.ide.wizard.RootNewProjectWizardStep
import com.intellij.openapi.project.Project
import java.nio.file.Paths
import javax.swing.Icon

/**
 * 静态 HTML 项目生成器。
 *
 * 在「新建项目」向导左侧列表里多一个「HTML 静态页面」入口，
 * 生成纯前端项目（index.html + css + js + README），
 * 双击 index.html 就能在浏览器打开，不需要任何后端环境。
 *
 * 只写磁盘文件，不依赖任何旗舰版 API，社区版（IC）即可用。
 */
class StaticHtmlGeneratorWizard : GeneratorNewProjectWizard {

    override val id: String get() = "static.html.template"

    override val name: String get() = "HTML 静态页面"

    // 用 IDE 自带的 Web 图标，避免额外加资源文件
    override val icon: Icon get() = AllIcons.Nodes.WebFolder

    override fun createStep(context: WizardContext): NewProjectWizardStep =
        NewProjectWizardChainStep(RootNewProjectWizardStep(context))
            .nextStep { NewProjectWizardBaseStep(it) }
            .nextStep { StaticHtmlProjectStep(it) }

    /**
     * 无设置项：只挂一条步骤链，在 setupProject 里往项目目录写模板文件。
     */
    class StaticHtmlProjectStep(private val parent: NewProjectWizardStep) : AbstractNewProjectWizardStep(parent) {

        override fun setupProject(project: Project) {
            val base = project.basePath ?: return
            StaticHtmlGenerator.generateFiles(Paths.get(base))
        }
    }
}
