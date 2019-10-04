package pl.dwojciechowski.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ex.ToolWindowManagerListener
import com.intellij.openapi.wm.impl.ToolWindowImpl
import com.intellij.ui.content.ContentFactory
import io.grpc.ManagedChannel
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.service.impl.LogViewerServiceImpl
import pl.dwojciechowski.ui.components.LogViewerPanel

class LogViewerPanelFactory : ToolWindowFactory, DumbAware {

    private lateinit var logService: LogViewerServiceImpl
    private lateinit var config: PluginConfiguration
    private lateinit var channel: ManagedChannel

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        config = ServiceManager.getService(project, PluginConfiguration::class.java)
        logService = ServiceManager.getService(project, LogViewerServiceImpl::class.java)

        val contentFactory = ContentFactory.SERVICE.getInstance()

        val logPane1 = LogViewerPanel(project)
        val logPane2 = LogViewerPanel(project)

        val content = contentFactory.createContent(logPane1, "Method Server", false)
        content.preferredFocusableComponent = logPane1

        val content2 = contentFactory.createContent(logPane2, "Background Method Server", false)
        content.preferredFocusableComponent = logPane2

        var logsEnabled = false;
        config.subjectLog.subscribe {
            logsEnabled = it
        }

        (toolWindow as ToolWindowImpl).setTabActions(object :
            DumbAwareAction("New Session", "Create new session", AllIcons.Actions.Checked) {
            override fun actionPerformed(e: AnActionEvent) {
                WindchillNotification.createNotification(project, config.hostname, PluginIcons.OK)
                config.subjectLog.onNext(!logsEnabled)
            }
        })
        toolWindow.setToHideOnEmptyContent(true)

        project.messageBus.connect().subscribe(ToolWindowManagerListener.TOPIC, object :
            ToolWindowManagerListener {
            override fun stateChanged() {
                if (toolWindow.isVisible && toolWindow.getContentManager().contentCount == 0) {
                    // open a new session if all tabs were closed manually
//                    createNewSession(myTerminalRunner, null)
                }
            }
        })

        toolWindow.contentManager.addContent(content)
        toolWindow.contentManager.addContent(content2)
    }

}