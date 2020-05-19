package pl.dwojciechowski.run.state

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.process.NopProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.Content
import pl.dwojciechowski.model.CommandBean
import pl.dwojciechowski.run.config.RemoteCommandConfigurationBase
import pl.dwojciechowski.service.RemoteService
import java.time.LocalTime.now

class RemoteCommandState(private val environment: ExecutionEnvironment) : RunProfileState {

    private val remoteServiceManager = RemoteService.getInstance(environment.project)

    override fun execute(executor: Executor?, runner: ProgramRunner<*>): ExecutionResult? {
        withCommandPane { toolWindow, content ->
            val contentManager = toolWindow.contentManager
            toolWindow.show()
            contentManager.setSelectedContent(content, true, false)
        }
        remoteServiceManager.executeStreaming(buildCommandBean())

        return DefaultExecutionResult(NopProcessHandler())
    }

    private fun buildCommandBean(): CommandBean {
        val configurationBase = environment.runProfile as RemoteCommandConfigurationBase
        return CommandBean(configurationBase.settings.command, configurationBase.settings.command, now())
    }

    private fun withCommandPane(method: (ToolWindow, Content) -> Unit) {
        val instance = ToolWindowManager.getInstance(environment.project)
        instance.getToolWindow("PLM Companion Log")?.let { toolWindow ->
            val contentManager = toolWindow.contentManager
            contentManager.getContent(2)?.let { content ->
                method(toolWindow, content)
            }
        }
    }


}