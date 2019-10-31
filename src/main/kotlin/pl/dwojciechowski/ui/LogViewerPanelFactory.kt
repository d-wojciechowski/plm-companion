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
import com.intellij.openapi.wm.impl.content.BaseLabel
import com.intellij.ui.content.ContentFactory
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.service.LogViewerService
import pl.dwojciechowski.ui.dialog.LogFileLocationDialog
import pl.dwojciechowski.ui.panel.LogViewerPanel
import pl.dwojciechowski.proto.Service.LogFileLocation.Source as SourceEnum

class LogViewerPanelFactory : ToolWindowFactory, DumbAware {

    private lateinit var logService: LogViewerService
    private lateinit var config: PluginConfiguration

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        config = ServiceManager.getService(project, PluginConfiguration::class.java)
        logService = ServiceManager.getService(project, LogViewerService::class.java)

        val contentFactory = ContentFactory.SERVICE.getInstance()
        val logPane1 = LogViewerPanel(project, SourceEnum.METHOD_SERVER)
        val logPane2 = LogViewerPanel(project, SourceEnum.BACKGROUND_METHOD_SERVER)

        val content = contentFactory.createContent(logPane1, "Method Server", false)
        content.preferredFocusableComponent = logPane1

        val content2 = contentFactory.createContent(logPane2, "Background Method Server", false)
        content.preferredFocusableComponent = logPane2

        toolWindow.contentManager.addContent(content)
        toolWindow.contentManager.addContent(content2)
        setActionsForLogsWindow(toolWindow, project, contentFactory)
    }

    private fun setActionsForLogsWindow(
        toolWindow: ToolWindow,
        project: Project,
        contentFactory: ContentFactory
    ) {
        (toolWindow as ToolWindowImpl).setTabActions(createNewTabAction(project, contentFactory, toolWindow))
        toolWindow.setTabDoubleClickActions(object : DumbAwareAction("", "", AllIcons.General.Add) {
            override fun actionPerformed(e: AnActionEvent) {
                val baseLabel = e.inputEvent.source as BaseLabel
                if (baseLabel.text.equals("Method Server", true) ||
                    baseLabel.text.equals("Background Method Server", true)
                ) {
                    return
                }
                toolWindow.contentManager.removeContent(baseLabel.content, true)
            }
        })
    }

    private fun createNewTabAction(
        project: Project,
        contentFactory: ContentFactory,
        toolWindow: ToolWindowImpl
    ): DumbAwareAction {
        return object :
            DumbAwareAction("New Window", "Create new log window", AllIcons.General.Add) {
            override fun actionPerformed(e: AnActionEvent) {
                val newPanel = LogViewerPanel(project, SourceEnum.CUSTOM)
                if (!LogFileLocationDialog(project, newPanel.logLocation).showAndGet()) {
                    return
                }
                val newContent = contentFactory.createContent(newPanel, newPanel.customLogFileLocation, false)
                newPanel.parentContent = newContent
                newContent.isCloseable = true
                newContent.preferredFocusableComponent = newPanel
                toolWindow.contentManager.addContent(newContent)
                toolWindow.contentManager.canCloseContents()
                toolWindow.contentManager.setSelectedContent(newContent)
            }
        }
    }

}