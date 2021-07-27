package pl.dwojciechowski.activities

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.util.text.VersionComparatorUtil
import pl.dwojciechowski.configuration.GlobalPluginConfiguration
import pl.dwojciechowski.model.PluginConstants
import pl.dwojciechowski.ui.dialog.NewVersionDialog

class NewVersionStartupActivity : StartupActivity, DumbAware {

    /**
     * New version inspired by Key Promoter X, and some code was taken from its implementation.
     * Thanks Patrick Scheibe!
     */
    override fun runActivity(project: Project) {
        val application = ApplicationManager.getApplication()
        val settings = application.getService(GlobalPluginConfiguration::class.java)

        val plugin = PluginManagerCore.getPlugins().find { it.pluginId == PluginId.getId(PluginConstants.PLUGIN_ID) }
        if (plugin != null) {
            val compare = VersionComparatorUtil.compare(settings.installedVersion, plugin.version)
            if (compare < 0) {
                application.invokeLater { NewVersionDialog(project).show() }
                settings.installedVersion = plugin.version
            }
        }
    }

}