package pl.dwojciechowski.service

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import pl.dwojciechowski.proto.Service

interface WncConnectorService {

    companion object {
        fun getInstance(project: Project): WncConnectorService {
            return ServiceManager.getService(project, WncConnectorService::class.java)
        }
    }

    fun stopWnc(): Service.Response
    fun startWnc(): Service.Response
    fun restartWnc(): Service.Response
    fun xconf(): Service.Response
    fun execCommand(command: Service.Command): Service.Response

}