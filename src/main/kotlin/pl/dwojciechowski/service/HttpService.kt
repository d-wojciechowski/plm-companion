package pl.dwojciechowski.service

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import pl.dwojciechowski.model.HttpStatusConfig
import pl.dwojciechowski.model.ServerStatus

interface HttpService {

    companion object {
        fun getInstance(project: Project): HttpService {
            return ServiceManager.getService(project, HttpService::class.java)
        }
    }

    fun getStatus(config: HttpStatusConfig): ServerStatus

}