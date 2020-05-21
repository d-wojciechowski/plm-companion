package pl.dwojciechowski.execution.command

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionException
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner

class RemoteCommandState(private val environment: ExecutionEnvironment) : RunProfileState {

    private val runProfile = environment.runProfile as RemoteCommandRunConfig

    override fun execute(executor: Executor?, runner: ProgramRunner<*>): ExecutionResult? {
        if (runProfile.settings.command.isEmpty()) {
            throw ExecutionException("Could not execute command, no command provided")
        }
        return DefaultExecutionResult(RemoteCommandProcessHandler(environment, executor))
    }

}