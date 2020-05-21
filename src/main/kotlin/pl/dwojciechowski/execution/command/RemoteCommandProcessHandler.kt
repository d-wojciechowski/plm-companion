package pl.dwojciechowski.execution.command

import com.intellij.execution.Executor
import com.intellij.execution.process.NopProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.wm.ToolWindowManager
import pl.dwojciechowski.model.CommandBean
import pl.dwojciechowski.service.RemoteService
import java.time.LocalTime

class RemoteCommandProcessHandler(
    private val environment: ExecutionEnvironment,
    private val executor: Executor?
) : NopProcessHandler() {

    private val remoteServiceManager = RemoteService.getInstance(environment.project)
    private val runProfile = environment.runProfile as RemoteCommandRunConfig

    private val command = CommandBean(runProfile.settings.command, runProfile.settings.command, LocalTime.now())

    override fun destroyProcess() {
        super.destroyProcess()
        switch()
    }

    override fun startNotify() {
        super.startNotify()
        ApplicationManager.getApplication().invokeLater {
            executeCommand()
        }
    }

    private fun executeCommand() {
        if (runProfile.settings.async.not()) {
            focusOnPluginTab()
            remoteServiceManager.executeStreaming(command) {
                destroyProcess()
            }
        } else {
            remoteServiceManager.executeStreaming(command)
            destroyProcess()
        }
    }

    private fun switch() {
        getToolWindow(executor?.toolWindowId)
            ?.let { toolWindow ->
                toolWindow.contentManager.selectedContent?.let {
                    toolWindow.contentManager.removeContent(it, true)
                    focusOnPluginTab()
                }
            }
    }

    private fun focusOnPluginTab() {
        getToolWindow("PLM Companion Log")?.let { toolWindow ->
            val contentManager = toolWindow.contentManager
            contentManager.getContent(2)?.let { content ->
                toolWindow.show()
                contentManager.setSelectedContent(content, true, false)
            }
        }
    }

    private fun getToolWindow(id: String?) = ToolWindowManager.getInstance(environment.project).getToolWindow(id)

}