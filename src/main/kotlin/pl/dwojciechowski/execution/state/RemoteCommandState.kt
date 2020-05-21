package pl.dwojciechowski.execution.state

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.process.NopProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.wm.ToolWindowManager
import pl.dwojciechowski.execution.config.RemoteCommandRunConfig
import pl.dwojciechowski.model.CommandBean
import pl.dwojciechowski.service.RemoteService
import java.time.LocalTime.now

class RemoteCommandState(private val environment: ExecutionEnvironment) : RunProfileState {

    private val remoteServiceManager = RemoteService.getInstance(environment.project)
    val runProfile = environment.runProfile as RemoteCommandRunConfig

    override fun execute(executor: Executor?, runner: ProgramRunner<*>): ExecutionResult? {
        return DefaultExecutionResult(object : NopProcessHandler() {
            override fun startNotify() {
                super.startNotify()
                if (runProfile.settings.async.not()) {
                    ApplicationManager.getApplication().invokeLater {
                        remoteServiceManager.executeStreaming(buildCommandBean()) {
                            if (runProfile.settings.async.not()) {
                                destroyProcess()
                            }
                        }
                    }
                } else {
                    remoteServiceManager.executeStreaming(buildCommandBean())
                    destroyProcess()
                }
            }

            override fun destroyProcess() {
                super.destroyProcess()
                switch()
            }

            private fun switch() {
                val contentManager1 = ToolWindowManager.getInstance(environment.project)
                    .getToolWindow(executor?.toolWindowId)?.contentManager
                contentManager1?.selectedContent?.let {
                    contentManager1.removeContent(it, true)
                    focusOnPluginTab()
                }
            }
        })
    }

    private fun buildCommandBean(): CommandBean {
        return CommandBean(runProfile.settings.command, runProfile.settings.command, now())
    }

    private fun focusOnPluginTab() {
        val instance = ToolWindowManager.getInstance(environment.project)
        instance.getToolWindow("PLM Companion Log")?.let { toolWindow ->
            val contentManager = toolWindow.contentManager
            contentManager.getContent(2)?.let { content ->
                toolWindow.show()
                contentManager.setSelectedContent(content, true, false)
            }
        }
    }

}