package pl.dwojciechowski.execution.state

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.process.NopProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import pl.dwojciechowski.execution.config.RemoteCommandRunConfig
import pl.dwojciechowski.model.CommandBean
import pl.dwojciechowski.service.RemoteService
import java.time.LocalTime.now

class RemoteCommandState(private val environment: ExecutionEnvironment) : RunProfileState {

    private val remoteServiceManager = RemoteService.getInstance(environment.project)

    override fun execute(executor: Executor?, runner: ProgramRunner<*>): ExecutionResult? {
        remoteServiceManager.executeStreaming(buildCommandBean())
//        environment.setCallback {
//            it.processHandler?.destroyProcess()
//        }


        return DefaultExecutionResult(object: NopProcessHandler(){
            override fun isProcessTerminating(): Boolean {
                return true
            }

            override fun isProcessTerminated(): Boolean {
                destroyProcess()
                return true
            }
        })
    }

    private fun buildCommandBean(): CommandBean {
        val configurationBase = environment.runProfile as RemoteCommandRunConfig
        return CommandBean(configurationBase.settings.command, configurationBase.settings.command, now())
    }

}