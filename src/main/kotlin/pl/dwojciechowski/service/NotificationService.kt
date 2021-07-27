package pl.dwojciechowski.service

import com.intellij.openapi.project.Project

interface NotificationService {

    companion object {
        fun getInstance(project: Project): NotificationService {
            return project.getService(NotificationService::class.java)
        }
    }

}