package pl.dwojciechowski.service.impl

import com.intellij.openapi.project.Project
import pl.dwojciechowski.model.ServerStatus
import pl.dwojciechowski.service.NotificationService
import pl.dwojciechowski.service.StatusService
import pl.dwojciechowski.ui.PLMPluginNotification

class NotificationServiceImpl(project: Project) : NotificationService {

    private val statusService = project.getService(StatusService::class.java)

    private var previousStatus = ServerStatus.DOWN

    init {
        statusService.getOutputSubject().subscribe { status ->
            when (status) {
                previousStatus -> Unit
                ServerStatus.AVAILABLE -> PLMPluginNotification.apacheOK(project)
                ServerStatus.RUNNING -> PLMPluginNotification.serverOK(project)
                else -> PLMPluginNotification.serverKO(project)
            }
            previousStatus = status
        }
    }

}