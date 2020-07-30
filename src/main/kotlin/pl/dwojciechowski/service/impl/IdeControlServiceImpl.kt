package pl.dwojciechowski.service.impl

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentManager
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.model.PluginConstants
import pl.dwojciechowski.service.IdeControlService

class IdeControlServiceImpl(private val project: Project) : IdeControlService {

    private val config = ServiceManager.getService(project, PluginConfiguration::class.java)

    override fun initCommandTab() {
        getToolWindow(PluginConstants.LOG_TAB_ID)?.let { toolWindow ->
            ApplicationManager.getApplication().invokeLater {
                if (!toolWindow.isVisible) {
                    toolWindow.show{}
                    toolWindow.hide{}
                }
            }
        }
    }

    override fun switchToCommandTab() {
        withCommandTab { toolWindow, contentManager, content ->
            ApplicationManager.getApplication().invokeLater {
                toolWindow.show{}
                contentManager.setSelectedContent(content, true, true)
            }

        }
    }

    override fun withCommandTab(doWith: (ToolWindow, ContentManager, Content) -> Unit) {
        getToolWindow(PluginConstants.LOG_TAB_ID)?.let { toolWindow ->
            val contentManager = toolWindow.contentManager
            contentManager.getContent(2)?.let { doWith(toolWindow, contentManager, it) }
        }
    }

    override fun withAutoOpen(action: () -> Unit) {
        ApplicationManager.getApplication().invokeLater {
            if (config.autoOpenCommandPane) {
                switchToCommandTab()
            }
            action.invoke()
        }
    }

    override fun getToolWindow(id: String?) = ToolWindowManager.getInstance(project).getToolWindow(id)

}