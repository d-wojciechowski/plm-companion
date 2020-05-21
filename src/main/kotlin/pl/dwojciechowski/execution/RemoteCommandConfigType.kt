package pl.dwojciechowski.execution

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationTypeBase
import pl.dwojciechowski.execution.command.RemoteCommandFactory
import pl.dwojciechowski.ui.PluginIcons

class RemoteCommandConfigType : ConfigurationTypeBase(
    id = "PLMCompanion.RemoteCommand",
    displayName = "Remote Command",
    description = "Remote Command execution configuration",
    icon = PluginIcons.PLUGIN
) {

    override fun getConfigurationFactories(): Array<ConfigurationFactory> {
        return arrayOf(RemoteCommandFactory(this))
    }

}