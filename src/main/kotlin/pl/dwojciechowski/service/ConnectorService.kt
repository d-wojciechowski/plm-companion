package pl.dwojciechowski.service

import com.intellij.openapi.project.Project
import io.rsocket.RSocket

interface ConnectorService {

    companion object {
        fun getInstance(project: Project): ConnectorService {
            return project.getService(ConnectorService::class.java)
        }
    }

    fun getConnection(): RSocket
    fun maxAttempts(): Long

}