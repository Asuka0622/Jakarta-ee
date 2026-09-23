package com.asuka.jakartae.generator

import com.intellij.icons.AllIcons
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.GeneratorNewProjectWizard
import com.intellij.ide.wizard.NewProjectWizardBaseStep
import com.intellij.ide.wizard.NewProjectWizardChainStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.ide.wizard.RootNewProjectWizardStep
import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindText
import java.nio.file.Paths
import javax.swing.Icon

/**
 * JSP 教学项目生成器。
 *
 * 在「新建项目」向导左侧列表里多一个「JSP 教学模板」入口，
 * 生成以 JSP 为核心的 Java Web 项目，包含 9 个循序渐进的示例页面。
 *
 * 只写磁盘文件，不依赖任何旗舰版 API，社区版（IC）即可用。
 */
class JspGeneratorWizard : GeneratorNewProjectWizard {

    override val id: String get() = "jsp.template"

    override val name: String get() = "JSP 教学模板"

    override val icon: Icon get() = AllIcons.FileTypes.Jsp

    override fun createStep(context: WizardContext): NewProjectWizardStep =
        NewProjectWizardChainStep(RootNewProjectWizardStep(context))
            .nextStep { NewProjectWizardBaseStep(it) }
            .nextStep { parent -> JspProjectStep(parent) }

    /**
     * 生成器的最后一步：在向导第二页多出一个「应用名」输入框。
     */
    class JspProjectStep(private val parent: NewProjectWizardStep) : AbstractNewProjectWizardStep(parent) {

        private val appName: GraphProperty<String> =
            propertyGraph.property(JspTemplateGenerator.DEFAULT_APP_NAME)

        override fun setupUI(builder: Panel) {
            if (appName.get() == JspTemplateGenerator.DEFAULT_APP_NAME) {
                appName.set(JspTemplateGenerator.sanitizeAppName(context.projectName))
            }

            builder.row("应用名：") {
                textField()
                    .bindText(appName)
                    .comment(
                        "默认与项目名相同，可改。只允许字母、数字、- 和 _，" +
                            "访问地址是 http://localhost:8080/（应用名）_war_exploded/"
                    )
            }
        }

        override fun setupProject(project: Project) {
            val base = project.basePath ?: return
            JspTemplateGenerator.generateFiles(Paths.get(base), appName.get())
        }
    }
}
