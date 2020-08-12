package pl.dwojciechowski.activities

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.util.text.VersionComparatorUtil
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.ui.dialog.NewVersionDialog

class NewVersionStartupActivity : StartupActivity, DumbAware {

    val PLUGIN_ID = "pl.dominikw.Windchill-Intellij-Plugin"

    /**
     * New version inspired by Key Promoter X, and some code was taken from its implementation.
     * Thanks Patrick Scheibe!
     */
    override fun runActivity(project: Project) {
        val application = ApplicationManager.getApplication()
        val settings = ServiceManager.getService(project, PluginConfiguration::class.java)

        val plugin = PluginManagerCore.getPlugins().find { it.pluginId == PluginId.getId(PLUGIN_ID) }
        if (plugin != null) {
            val compare = VersionComparatorUtil.compare(settings.installedVersion, plugin.version)
            if (compare < 0) {
                application.invokeLater { NewVersionDialog(project).show() }
                settings.installedVersion = plugin.version
            }
        }
    }

}