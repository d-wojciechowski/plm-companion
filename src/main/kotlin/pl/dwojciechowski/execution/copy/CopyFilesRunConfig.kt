package pl.dwojciechowski.execution.copy

import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializer
import org.jdom.Element
import pl.dwojciechowski.i18n.PluginBundle.getMessage

class CopyFilesRunConfig(
    project: Project,
    factory: ConfigurationFactory,
    name: String
) : RunConfigurationBase<CopyFilesRunConfig>(project, factory, name) {

    var settings = CopyFilesSettings()

    override fun clone(): RunConfiguration {
        val runConfiguration = super.clone()
        (runConfiguration as CopyFilesRunConfig).settings = CopyFilesSettings()
        return runConfiguration
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return CopyFilesSettingsEditor(project)
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return CopyFilesState(environment)
    }

    override fun checkConfiguration() {
        if (settings.command.isEmpty()) {
            throw RuntimeConfigurationWarning(getMessage("runconfig.remotecommand.error.empty"))
        }
    }

    override fun readExternal(element: Element) {
        super.readExternal(element)

        element.getChild(CopyFilesSettings.TAG)?.let {
            settings = XmlSerializer.deserialize(it, CopyFilesSettings::class.java)
        }
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        element.addContent(XmlSerializer.serialize(settings))
    }

}