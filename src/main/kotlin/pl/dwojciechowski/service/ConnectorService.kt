package pl.dwojciechowski.service

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import io.rsocket.RSocket

interface ConnectorService {

    companion object {
        fun getInstance(project: Project): ConnectorService =
            ServiceManager.getService(project, ConnectorService::class.java)
    }

    fun getConnection(): RSocket

}