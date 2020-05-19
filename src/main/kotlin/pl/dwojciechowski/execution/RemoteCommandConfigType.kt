package pl.dwojciechowski.execution

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationTypeBase
import pl.dwojciechowski.execution.factory.RemoteCommandFactory
import pl.dwojciechowski.ui.PluginIcons

class RemoteCommandConfigType : ConfigurationTypeBase(
    "PLMCompanion.RemoteCommand", "Remote Command", "Remote Command execution configuration", PluginIcons.PLUGIN
) {

    override fun getConfigurationFactories(): Array<ConfigurationFactory> {
        return arrayOf(RemoteCommandFactory(this))
    }

}