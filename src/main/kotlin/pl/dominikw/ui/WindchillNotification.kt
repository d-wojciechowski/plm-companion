package pl.dominikw.ui

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType

internal object WindchillNotification {

    private val GROUP = NotificationGroup(
        "Windchill", NotificationDisplayType.BALLOON, false
    )

    fun serverOK() {
        GROUP.createNotification("Windchill is UP!", NotificationType.INFORMATION)
            .setIcon(PluginIcons.OK).notify(null)
    }

}
