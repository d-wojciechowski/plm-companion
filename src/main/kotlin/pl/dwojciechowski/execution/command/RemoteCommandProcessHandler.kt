package pl.dwojciechowski.execution.command

import com.intellij.execution.Executor
import com.intellij.execution.process.NopProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.application.ApplicationManager
import pl.dwojciechowski.model.CommandBean
import pl.dwojciechowski.service.IdeControlService
import pl.dwojciechowski.service.RemoteService
import java.time.LocalTime

class RemoteCommandProcessHandler(
    environment: ExecutionEnvironment,
    private val executor: Executor?
) : NopProcessHandler() {

    private val remoteServiceManager = RemoteService.getInstance(environment.project)
    private val ideControlService = IdeControlService.getInstance(environment.project)
    private val runProfile = environment.runProfile as RemoteCommandRunConfig

    private val command = CommandBean(runProfile.settings.command, runProfile.settings.command, executionTime = LocalTime.now())

    override fun destroyProcess() {
        super.destroyProcess()
        switchToSelectedTab()
    }

    override fun startNotify() {
        super.startNotify()
        ApplicationManager.getApplication().invokeLater {
            executeCommand()
        }
    }

    private fun executeCommand() {
        if (runProfile.settings.async.not()) {
            ideControlService.switchToCommandTab()
            remoteServiceManager.executeStreaming(command) {
                destroyProcess()
            }
        } else {
            remoteServiceManager.executeStreaming(command)
            destroyProcess()
        }
    }

    private fun switchToSelectedTab() {
        ideControlService.getToolWindow(executor?.toolWindowId)
            ?.let { toolWindow ->
                toolWindow.contentManager.selectedContent?.let {
                    toolWindow.contentManager.removeContent(it, true)
                    ideControlService.switchToCommandTab()
                }
            }
    }

}