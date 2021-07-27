package pl.dwojciechowski.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ex.ToolWindowEx
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import pl.dwojciechowski.configuration.ProjectPluginConfiguration
import pl.dwojciechowski.i18n.PluginBundle.getMessage
import pl.dwojciechowski.service.LogViewerService
import pl.dwojciechowski.ui.dialog.LogFileLocationDialog
import pl.dwojciechowski.ui.panel.CommandLogPanel
import pl.dwojciechowski.ui.panel.LogViewerPanel
import pl.dwojciechowski.proto.files.LogFileLocation.Source as SourceEnum

class LogViewerPanelFactory : ToolWindowFactory, DumbAware {

    private lateinit var logService: LogViewerService
    private lateinit var config: ProjectPluginConfiguration

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        config = project.getService(ProjectPluginConfiguration::class.java)
        logService = project.getService(LogViewerService::class.java)

        val contentFactory = ContentFactory.SERVICE.getInstance()
        val logPane1 = LogViewerPanel(project, SourceEnum.METHOD_SERVER)
        val logPane2 = LogViewerPanel(project, SourceEnum.BACKGROUND_METHOD_SERVER)
        val commandPanel = CommandLogPanel(project)

        val content = contentFactory.createContent(logPane1, getMessage("ui.tab.log.ms.display"), false)
        content.preferredFocusableComponent = logPane1
        content.isCloseable = false

        val content2 = contentFactory.createContent(logPane2, getMessage("ui.tab.log.bms.display"), false)
        content2.preferredFocusableComponent = logPane2
        content2.isCloseable = false

        val commandContentPanel =
            contentFactory.createContent(commandPanel, getMessage("ui.tab.log.commands.display"), false)
        commandContentPanel.preferredFocusableComponent = commandPanel
        commandContentPanel.isCloseable = false

        toolWindow.contentManager.addContent(content)
        toolWindow.contentManager.addContent(content2)
        toolWindow.contentManager.addContent(commandContentPanel)
        if (config.persistCustomTabs) {
            config.customTabs.toList().forEach { customTab ->
                toolWindow.contentManager.addContent(createCustomTabContent(project, contentFactory, customTab))
            }
        }
        (toolWindow as ToolWindowEx).setTabActions(createNewTabAction(project, contentFactory, toolWindow))
    }

    private fun createNewTabAction(
        project: Project,
        contentFactory: ContentFactory,
        toolWindow: ToolWindowEx
    ): DumbAwareAction {
        return object :
            DumbAwareAction(
                getMessage("ui.tab.log.custom.text"),
                getMessage("ui.tab.log.custom.description"),
                AllIcons.General.Add
            ) {
            override fun actionPerformed(e: AnActionEvent) {
                val logFileDialog = LogFileLocationDialog(project, fileOnlySelection = true)
                if (!logFileDialog.showAndGet()) {
                    return
                }
                val newContent = createCustomTabContent(project, contentFactory, logFileDialog.result)
                toolWindow.contentManager.addContent(newContent)
                if (config.persistCustomTabs) {
                    config.customTabs.add(logFileDialog.result)
                }
                toolWindow.contentManager.setSelectedContent(newContent)
            }
        }
    }

    private fun createCustomTabContent(project: Project, contentFactory: ContentFactory, tabName: String): Content {
        val newPanel = LogViewerPanel(project, SourceEnum.CUSTOM, tabName)
        val newContent = contentFactory.createContent(newPanel, tabName, false)
        newPanel.parentContent = newContent
        newContent.preferredFocusableComponent = newPanel
        newContent.setDisposer {
            config.customTabs = config.customTabs.filter { it != tabName }.toMutableList()
        }
        return newContent
    }

}