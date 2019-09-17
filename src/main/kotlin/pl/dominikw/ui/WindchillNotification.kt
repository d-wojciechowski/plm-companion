package pl.dominikw.ui

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

internal object WindchillNotification {

    private val GROUP = NotificationGroup(
        "Windchill", NotificationDisplayType.BALLOON, true
    )

    fun serverOK(project: Project) {
        GROUP.createNotification("Windchill is UP!", NotificationType.INFORMATION)
            .setImportant(true)
            .setIcon(PluginIcons.OK)
            .notify(project)
    }

    fun serverKO(project: Project) {
        GROUP.createNotification("Windchill is DOWN!", NotificationType.INFORMATION)
            .setImportant(true)
            .setIcon(PluginIcons.KO)
            .notify(project)
    }

}
