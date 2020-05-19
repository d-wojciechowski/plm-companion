package pl.dwojciechowski.execution.execution

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.Content
import pl.dwojciechowski.execution.config.RemoteCommandRunConfig

class CommandPaneRunner : ProgramRunner<RunnerSettings> {

    val EXECUTOR_ID = "RemoteCommandExecutionRunner"

    @Throws(ExecutionException::class)
    override fun execute(environment: ExecutionEnvironment) {
        withCommandPane(environment) { toolWindow, content ->
            val contentManager = toolWindow.contentManager
            toolWindow.show()
            contentManager.setSelectedContent(content, true, false)
        }
        environment.state?.execute(environment.executor, this)
    }

    override fun getRunnerId(): String {
        return EXECUTOR_ID
    }

    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        return DefaultRunExecutor.EXECUTOR_ID == executorId && profile is RemoteCommandRunConfig
    }

    private fun withCommandPane(environment: ExecutionEnvironment, method: (ToolWindow, Content) -> Unit) {
        val instance = ToolWindowManager.getInstance(environment.project)
        instance.getToolWindow("PLM Companion Log")?.let { toolWindow ->
            val contentManager = toolWindow.contentManager
            contentManager.getContent(2)?.let { content ->
                method(toolWindow, content)
            }
        }
    }
}