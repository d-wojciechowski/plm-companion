package pl.dwojciechowski.ui

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import javax.swing.Icon

internal object WindchillNotification {

    private val GROUP = NotificationGroup(
        "Windchill", NotificationDisplayType.BALLOON, true
    )

    fun serverOK(project: Project) {
        createNotification(project,"Windchill is OK!", PluginIcons.OK)
    }

    fun serverKO(project: Project) {
        createNotification(project,"Windchill is DOWN!", PluginIcons.KO)
    }

    fun settingsSaved(project: Project) {
        createNotification(project,"Settings Saved", PluginIcons.OK)
    }

    private fun createNotification(project: Project, text: String, icon: Icon) {
        GROUP.createNotification(text, NotificationType.INFORMATION)
            .setImportant(true)
            .setIcon(icon)
            .notify(project)
    }
}
