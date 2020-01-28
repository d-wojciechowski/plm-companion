package pl.dwojciechowski.ui

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import pl.dwojciechowski.ui.panel.PLMCompanionPanel

class MainSubWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val toolWindowBuilder = PLMCompanionPanel(project)
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val toolWindowContent = toolWindowBuilder.content
        val content = contentFactory.createContent(toolWindowContent, "", false)
        content.preferredFocusableComponent = toolWindowContent
        toolWindow.contentManager.addContent(content)
    }

}