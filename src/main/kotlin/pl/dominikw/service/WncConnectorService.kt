package pl.dominikw.service

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project

interface WncConnectorService {
    companion object {
        fun getInstance(project: Project): WncConnectorService {
            return ServiceManager.getService(project, WncConnectorService::class.java)
        }
    }

    fun stopWnc(hostname: String)
    fun startWnc(hostname: String)
    fun restartWnc(hostname: String)
}