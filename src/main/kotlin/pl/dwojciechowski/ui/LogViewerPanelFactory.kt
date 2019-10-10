package pl.dwojciechowski.ui

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.impl.ToolWindowImpl
import com.intellij.ui.content.ContentFactory
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.proto.Service
import pl.dwojciechowski.service.LogViewerService
import pl.dwojciechowski.ui.actions.ToggleLoggingAction
import pl.dwojciechowski.ui.panel.LogViewerPanel

class LogViewerPanelFactory : ToolWindowFactory, DumbAware {

    private lateinit var logService: LogViewerService
    private lateinit var config: PluginConfiguration

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        config = ServiceManager.getService(project, PluginConfiguration::class.java)
        logService = ServiceManager.getService(project, LogViewerService::class.java)

        val contentFactory = ContentFactory.SERVICE.getInstance()
        val logPane1 = LogViewerPanel(project, Service.LogFileLocation.Source.METHOD_SERVER)
        val logPane2 = LogViewerPanel(project, Service.LogFileLocation.Source.BACKGROUND_METHOD_SERVER)

        val content = contentFactory.createContent(logPane1, "Method Server", false)
        content.preferredFocusableComponent = logPane1

        val content2 = contentFactory.createContent(logPane2, "Background Method Server", false)
        content.preferredFocusableComponent = logPane2

        toolWindow.contentManager.addContent(content)
        toolWindow.contentManager.addContent(content2)

        var logsEnabled = false
        config.subjectLog.subscribe {
            logsEnabled = it
        }
        ToggleLoggingAction(toolWindow as ToolWindowImpl) {
            logsEnabled = !logsEnabled
            config.subjectLog.onNext(logsEnabled)
        }
    }

}