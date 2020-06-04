package pl.dwojciechowski.service

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project

interface NotificationService {

    companion object {
        fun getInstance(project: Project): NotificationService {
            return ServiceManager.getService(project, NotificationService::class.java)
        }
    }

}