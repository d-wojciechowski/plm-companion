package pl.dwojciechowski.service.impl

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.dwojciechowski.model.ServerStatus
import pl.dwojciechowski.service.NotificationService
import pl.dwojciechowski.service.StatusService
import pl.dwojciechowski.ui.PLMPluginNotification

class NotificationServiceImpl(project: Project) : NotificationService {

    private val statusService = ServiceManager.getService(project, StatusService::class.java)

    private var previousStatus = ServerStatus.DOWN

    init {
        GlobalScope.launch {
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

}