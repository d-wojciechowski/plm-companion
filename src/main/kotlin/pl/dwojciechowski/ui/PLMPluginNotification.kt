package pl.dwojciechowski.ui

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import pl.dwojciechowski.model.CommandBean
import javax.swing.Icon

internal object PLMPluginNotification {

    private val GROUP = NotificationGroup(
        "Windchill", NotificationDisplayType.BALLOON, true, null, null
    )

    fun serverOK(project: Project) {
        notify(project, "Windchill is OK!", PluginIcons.RUNNING)
    }

    fun serverKO(project: Project) {
        notify(project, "Windchill is DOWN!", PluginIcons.ERROR)
    }

    fun apacheOK(project: Project) {
        notify(project, "Apache is OK, Windchill is DOWN!", PluginIcons.WARNING)
    }

    fun settingsSaved(project: Project) {
        notify(project, "Settings Saved", PluginIcons.CONFIRMATION)
    }

    fun notifyCommandBeanChange(project: Project, commandBean: CommandBean){

    }

    fun notify(project: Project, text: String, icon: Icon) {
        GROUP.createNotification(text, NotificationType.INFORMATION)
            .setImportant(true)
            .setIcon(PluginIcons.scaleToSize(icon, 18))
            .notify(project)
    }

}
