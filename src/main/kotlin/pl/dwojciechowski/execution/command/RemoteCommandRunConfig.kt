package pl.dwojciechowski.execution.command

import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializer
import org.jdom.Element

class RemoteCommandRunConfig(
    project: Project,
    factory: ConfigurationFactory,
    name: String
) : RunConfigurationBase<RemoteCommandRunConfig>(project, factory, name) {

    var settings = RemoteCommandSettings()

    override fun clone(): RunConfiguration {
        val runConfiguration = super.clone()
        (runConfiguration as RemoteCommandRunConfig).settings = RemoteCommandSettings()
        return runConfiguration
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return RemoteCommandSettingsEditor(project)
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return RemoteCommandState(environment)
    }

    override fun checkConfiguration() {
        if (settings.command.isEmpty()) {
            throw RuntimeConfigurationWarning("Command may not be empty, provide non empty command")
        }
    }

    override fun readExternal(element: Element) {
        super.readExternal(element)

        element.getChild(RemoteCommandSettings.TAG)?.let {
            settings = XmlSerializer.deserialize(it, RemoteCommandSettings::class.java)
        }
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        element.addContent(XmlSerializer.serialize(settings))
    }

}