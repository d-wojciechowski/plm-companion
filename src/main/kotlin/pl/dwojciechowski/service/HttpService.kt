package pl.dwojciechowski.service

import com.intellij.openapi.components.ServiceManager
import pl.dwojciechowski.model.ServerStatus

interface HttpService {

    companion object {
        fun getInstance(): HttpService {
            return ServiceManager.getService(HttpService::class.java)
        }
    }

    abstract fun getStatus(targetUrl: String, login: String, password: String): ServerStatus
}