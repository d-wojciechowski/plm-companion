package pl.dwojciechowski.service

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import pl.dwojciechowski.model.CommandConfig

interface WncConnectorService {

    companion object {
        fun getInstance(project: Project): WncConnectorService {
            return ServiceManager.getService(project, WncConnectorService::class.java)
        }
    }

    fun stopWnc(cfg: CommandConfig)
    fun startWnc(cfg: CommandConfig)
    fun restartWnc(cfg: CommandConfig)

}