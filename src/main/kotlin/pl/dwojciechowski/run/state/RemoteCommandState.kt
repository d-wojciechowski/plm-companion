package pl.dwojciechowski.run.state

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.process.NopProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import pl.dwojciechowski.model.CommandBean
import pl.dwojciechowski.run.config.RemoteCommandConfigurationBase
import pl.dwojciechowski.service.RemoteService
import java.time.LocalTime.now

class RemoteCommandState(private val environment: ExecutionEnvironment) : RunProfileState {

    private val remoteServiceManager = RemoteService.getInstance(environment.project)

    override fun execute(executor: Executor?, runner: ProgramRunner<*>): ExecutionResult? {
        remoteServiceManager.executeStreaming(buildCommandBean())
        return DefaultExecutionResult(NopProcessHandler())
    }

    private fun buildCommandBean(): CommandBean {
        val configurationBase = environment.runProfile as RemoteCommandConfigurationBase
        return CommandBean(configurationBase.settings.command, configurationBase.settings.command, now())
    }

}