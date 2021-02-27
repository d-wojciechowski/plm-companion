package pl.dwojciechowski.execution.copy

import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionException
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import pl.dwojciechowski.i18n.PluginBundle.getMessage

class CopyFilesState(private val environment: ExecutionEnvironment) : RunProfileState {

    private val runProfile = environment.runProfile as CopyFilesRunConfig

    override fun execute(executor: Executor?, runner: ProgramRunner<*>): ExecutionResult? {
        if (runProfile.settings.folderConfigs.isEmpty()) {
            throw ExecutionException(getMessage("runconfig.copyfiles.error.empty"))
        }
        return DefaultExecutionResult(CopyFilesProcessHandler(environment, executor))
    }

}