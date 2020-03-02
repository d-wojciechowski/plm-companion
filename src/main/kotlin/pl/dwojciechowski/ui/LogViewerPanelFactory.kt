package pl.dwojciechowski.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.impl.ToolWindowImpl
import com.intellij.ui.content.ContentFactory
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.service.LogViewerService
import pl.dwojciechowski.ui.dialog.LogFileLocationDialog
import pl.dwojciechowski.ui.panel.CommandLogPanel
import pl.dwojciechowski.ui.panel.LogViewerPanel
import pl.dwojciechowski.proto.files.LogFileLocation.Source as SourceEnum

class LogViewerPanelFactory : ToolWindowFactory, DumbAware {

    private lateinit var logService: LogViewerService
    private lateinit var config: PluginConfiguration

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        config = ServiceManager.getService(project, PluginConfiguration::class.java)
        logService = ServiceManager.getService(project, LogViewerService::class.java)

        val contentFactory = ContentFactory.SERVICE.getInstance()
        val logPane1 = LogViewerPanel(project, SourceEnum.METHOD_SERVER)
        val logPane2 = LogViewerPanel(project, SourceEnum.BACKGROUND_METHOD_SERVER)
        val commandPanel = CommandLogPanel(project)

        val content = contentFactory.createContent(logPane1, "Method Server", false)
        content.preferredFocusableComponent = logPane1
        content.isCloseable = false

        val content2 = contentFactory.createContent(logPane2, "Background Method Server", false)
        content2.preferredFocusableComponent = logPane2
        content2.isCloseable = false

        val commandContentPanel = contentFactory.createContent(commandPanel, "Commands", false)
        commandContentPanel.preferredFocusableComponent = commandPanel
        commandContentPanel.isCloseable = false

        toolWindow.contentManager.addContent(content)
        toolWindow.contentManager.addContent(content2)
        toolWindow.contentManager.addContent(commandContentPanel)
        (toolWindow as ToolWindowImpl).setTabActions(createNewTabAction(project, contentFactory, toolWindow))
    }

    private fun createNewTabAction(
        project: Project,
        contentFactory: ContentFactory,
        toolWindow: ToolWindowImpl
    ): DumbAwareAction {
        return object :
            DumbAwareAction("New Window", "Create new log window", AllIcons.General.Add) {
            override fun actionPerformed(e: AnActionEvent) {
                val logFileDialog = LogFileLocationDialog(project)
                if (!logFileDialog.showAndGet()) {
                    return
                }
                val newPanel = LogViewerPanel(project, SourceEnum.CUSTOM, logFileDialog.result)
                val newContent = contentFactory.createContent(newPanel, logFileDialog.result, false)
                newPanel.parentContent = newContent
                newContent.preferredFocusableComponent = newPanel
                toolWindow.contentManager.addContent(newContent)
                toolWindow.contentManager.setSelectedContent(newContent)
            }
        }
    }

}