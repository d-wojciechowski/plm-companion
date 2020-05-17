package pl.dwojciechowski.listener

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import pl.dwojciechowski.service.NotificationService
import pl.dwojciechowski.service.RemoteService
import pl.dwojciechowski.service.StatusService

class ProjectChangedListener : ProjectManagerListener {

    override fun projectOpened(project: Project) {
        //Just init on project open
        ServiceManager.getService(project, NotificationService::class.java)
        ServiceManager.getService(project, RemoteService::class.java)
        ServiceManager.getService(project, StatusService::class.java)
    }

}