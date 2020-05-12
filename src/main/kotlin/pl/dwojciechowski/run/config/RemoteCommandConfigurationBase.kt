package pl.dwojciechowski.run.config

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import pl.dwojciechowski.run.editor.RemoteCommandSettingsEditor
import pl.dwojciechowski.run.state.RemoteCommandState

class RemoteCommandConfigurationBase(
    project: Project,
    factory: ConfigurationFactory,
    name: String
) : RunConfigurationBase<RemoteCommandConfigurationBase>(project, factory, name) {

    var settings = RemoteCommandSettings("")

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return RemoteCommandSettingsEditor(project)
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return RemoteCommandState(environment)
    }

    override fun checkConfiguration() {
    }

    data class RemoteCommandSettings(val command: String) : Cloneable {
        val TAG = "RemoteCommandSettings"
    }

}