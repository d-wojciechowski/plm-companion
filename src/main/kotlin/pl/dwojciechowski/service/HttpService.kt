package pl.dwojciechowski.service

import com.intellij.openapi.components.ServiceManager
import pl.dwojciechowski.model.HttpStatusConfig
import pl.dwojciechowski.model.ServerStatus

interface HttpService {

    companion object {
        fun getInstance(): HttpService {
            return ServiceManager.getService(HttpService::class.java)
        }
    }

    fun getStatus(config: HttpStatusConfig): ServerStatus

}