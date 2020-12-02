package pl.dwojciechowski.ui

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import pl.dwojciechowski.i18n.PluginBundle.getMessage
import javax.swing.Icon

internal object PLMPluginNotification {

    private val GROUP = NotificationGroup(
        "Windchill", NotificationDisplayType.BALLOON, true, null, null
    )

    fun serverOK(project: Project) {
        notify(project, getMessage("ui.notification.server.ok"), PluginIcons.RUNNING)
    }

    fun serverKO(project: Project) {
        notify(project, getMessage("ui.notification.server.ko"), PluginIcons.ERROR)
    }

    fun apacheOK(project: Project) {
        notify(project, getMessage("ui.notification.apache.ok"), PluginIcons.WARNING)
    }

    fun settingsSaved(project: Project) {
        notify(project, getMessage("ui.notification.settings.saved"), PluginIcons.CONFIRMATION)
    }

    fun notify(project: Project, text: String, icon: Icon) {
        GROUP.createNotification(text, NotificationType.INFORMATION)
            .setImportant(true)
            .setIcon(PluginIcons.scaleToSize(icon, 18))
            .notify(project)
    }

}
