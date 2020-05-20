package pl.dwojciechowski.execution.execution

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import pl.dwojciechowski.execution.config.RemoteCommandRunConfig

class CommandPaneRunner : ProgramRunner<RunnerSettings> {

    val EXECUTOR_ID = "RemoteCommandExecutionRunner"

    @Throws(ExecutionException::class)
    override fun execute(environment: ExecutionEnvironment) {

        environment.state?.execute(environment.executor, this)
    }

    override fun getRunnerId(): String {
        return EXECUTOR_ID
    }

    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        return DefaultRunExecutor.EXECUTOR_ID == executorId && profile is RemoteCommandRunConfig
    }


}