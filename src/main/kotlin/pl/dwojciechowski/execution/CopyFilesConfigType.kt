package pl.dwojciechowski.execution

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationTypeBase
import pl.dwojciechowski.execution.copy.CopyFilesFactory
import pl.dwojciechowski.i18n.PluginBundle
import pl.dwojciechowski.ui.PluginIcons

class CopyFilesConfigType : ConfigurationTypeBase(
    id = "PLMCompanion.CopyFiles",
    displayName = PluginBundle.getMessage("runconfig.copyfiles.displayname"),
    description = PluginBundle.getMessage("runconfig.copyfiles.description"),
    icon = PluginIcons.PLUGIN_SAVE
) {

    override fun getConfigurationFactories(): Array<ConfigurationFactory> {
        return arrayOf(CopyFilesFactory(this))
    }

}