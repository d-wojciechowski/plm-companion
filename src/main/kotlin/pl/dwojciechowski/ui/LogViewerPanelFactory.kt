package pl.dwojciechowski.ui

import com.intellij.ide.actions.ToggleToolbarAction
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ex.ToolWindowEx
import com.intellij.ui.content.ContentFactory
import pl.dwojciechowski.ui.panel.LogViewerPanel

class LogViewerPanelFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val toolWindowBuilder = LogViewerPanel(project)
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val toolWindowContent = toolWindowBuilder.content
        val content = contentFactory.createContent(toolWindowContent, "", false)
        content.preferredFocusableComponent = toolWindowContent
        toolWindow.contentManager.addContent(content)
        val decorator = (toolWindow as ToolWindowEx).decorator
        val instance = PropertiesComponent.getInstance(project)
        ToggleToolbarAction.setToolbarVisible(toolWindow, instance, true)
        decorator.setHeaderVisible(false)
    }
}