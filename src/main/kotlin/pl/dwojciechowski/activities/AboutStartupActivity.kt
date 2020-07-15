package pl.dwojciechowski.activities

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import pl.dwojciechowski.ui.dialog.WelcomeDialog


class AboutStartupActivity : StartupActivity, DumbAware {

    override fun runActivity(project: Project) {
        if (ApplicationManager.getApplication().isUnitTestMode) return
        val application = ApplicationManager.getApplication()
        //        final KeyPromoterSettings settings = ServiceManager.getService(KeyPromoterSettings.class);
//        final String installedVersion = settings.getInstalledVersion();

//        final IdeaPluginDescriptor plugin = PluginManagerCore.getPlugin(PluginId.getId("Key Promoter X"));
//        if (installedVersion != null && plugin != null) {
//            final int compare = VersionComparatorUtil.compare(installedVersion, plugin.getVersion());

//            if (compare < 0) {
        application.invokeLater { WelcomeDialog(project).showAndGet() }
        //                settings.setInstalledVersion(plugin.getVersion());
//            }
    }

}