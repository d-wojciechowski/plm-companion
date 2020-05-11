package pl.dwojciechowski.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationTypeBase
import pl.dwojciechowski.run.factory.RemoteCommandFactory
import pl.dwojciechowski.ui.PluginIcons

class RemoteCommandConfigurationType : ConfigurationTypeBase(
    "PLMCompanion.RemoteCommand", "Remote Command", "TBD", PluginIcons.PLUGIN
) {

    override fun getConfigurationFactories(): Array<ConfigurationFactory> {
        return arrayOf(RemoteCommandFactory(this))
    }

}