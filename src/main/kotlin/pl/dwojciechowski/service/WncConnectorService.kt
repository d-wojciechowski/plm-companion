package pl.dwojciechowski.service

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import pl.dwojciechowski.model.CommandConfig
import pl.dwojciechowski.proto.Service

interface WncConnectorService {

    companion object {
        fun getInstance(project: Project): WncConnectorService {
            return ServiceManager.getService(project, WncConnectorService::class.java)
        }
    }

    fun stopWnc(cfg: CommandConfig): Service.Response
    fun startWnc(cfg: CommandConfig): Service.Response
    fun restartWnc(cfg: CommandConfig) : Service.Response
    fun xconf(cfg: CommandConfig): Service.Response

}