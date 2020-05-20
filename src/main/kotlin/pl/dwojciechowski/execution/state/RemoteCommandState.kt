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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.dwojciechowski.execution.config.RemoteCommandRunConfig
import pl.dwojciechowski.model.CommandBean
import pl.dwojciechowski.service.RemoteService
import java.time.LocalTime.now

class RemoteCommandState(private val environment: ExecutionEnvironment) : RunProfileState {

    private val remoteServiceManager = RemoteService.getInstance(environment.project)

    override fun execute(executor: Executor?, runner: ProgramRunner<*>): ExecutionResult? {
        remoteServiceManager.executeStreaming(buildCommandBean())

        return DefaultExecutionResult(object : NopProcessHandler() {
            override fun startNotify() {
                super.startNotify()

                GlobalScope.launch {
                    destroyProcess()
                    val contentManager1 = ToolWindowManager.getInstance(environment.project)
                        .getToolWindow(executor?.toolWindowId)?.contentManager
                    contentManager1?.selectedContent?.let {
                        ApplicationManager.getApplication().invokeLater {
                            contentManager1.removeContent(it, true)
                            focusOnPluginTab()
                        }
                    }
                }
            }
        })
    }

    private fun buildCommandBean(): CommandBean {
        val configurationBase = environment.runProfile as RemoteCommandRunConfig
        return CommandBean(configurationBase.settings.command, configurationBase.settings.command, now())
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