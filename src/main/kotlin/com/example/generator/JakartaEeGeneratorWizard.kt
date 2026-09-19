package com.example.generator

import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.GeneratorNewProjectWizard
import com.intellij.ide.wizard.NewProjectWizardBaseStep
import com.intellij.ide.wizard.NewProjectWizardChainStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.ide.wizard.RootNewProjectWizardStep
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import java.nio.file.Paths
import javax.swing.Icon

/**
 * IntelliJ 2026 新建项目「生成器」入口。
 *
 * 2026 版把旧扩展点 com.intellij.directoryProjectGenerator 移出新建向导，改为
 * com.intellij.newProjectWizard.generator -> com.intellij.ide.wizard.GeneratorNewProjectWizard
 * 才能让条目出现在左侧「生成器」面板（与 Spring Boot / Ktor / 官方 Jakarta EE 同级）。
 *
 * 只写磁盘文件（pom.xml / web.xml / Servlet / index.html / README.md），
 * 不碰任何旗舰版私有 API，因此社区版（IC）即可用。
 *
 * 模板内容不写在本文件里，而是放在插件 jar 的 resources/templates/ 下原样拷贝，
 * 好处是模板文件保持真实扩展名，编辑器里能正常高亮、也没有字符串转义问题。
 */
class JakartaEeGeneratorWizard : GeneratorNewProjectWizard {

    // 关键：2026 版将抽象成员声明为属性（val id/name/icon），不是 getId()/getName() 方法
    override val id: String get() = "jakartaee.web.template"

    // 刻意避开官方内置「Jakarta EE」，避免在 Ultimate 上被同名官方生成器压掉
    override val name: String get() = "Jakarta EE Web 模板"

    // 自定义图标：resources/icons/jakartaEe.png（含 @2x 高分屏变体）
    override val icon: Icon get() = IconLoader.getIcon("/icons/jakartaEe.png", javaClass)

    override fun createStep(context: WizardContext): NewProjectWizardStep =
        NewProjectWizardChainStep(RootNewProjectWizardStep(context))
            .nextStep { NewProjectWizardBaseStep(it) }
            .nextStep { parent -> JakartaEeProjectStep(parent) }

    /**
     * 无设置项：仅挂一条步骤链，在 setupProject 里往项目目录写模板文件。
     */
    class JakartaEeProjectStep(private val parent: NewProjectWizardStep) : AbstractNewProjectWizardStep(parent) {

        override fun setupProject(project: Project) {
            // 向导执行到此时项目已创建，basePath 即用户选择的目录
            val base = project.basePath ?: return
            // 生成逻辑在 TemplateGenerator 里（不依赖平台，可脱离 IDE 单独测试）
            TemplateGenerator.generateFiles(Paths.get(base))
        }
    }
}
