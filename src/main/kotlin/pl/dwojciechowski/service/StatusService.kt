package pl.dwojciechowski.service

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import io.reactivex.rxjava3.subjects.Subject
import pl.dwojciechowski.model.HttpStatusConfig
import pl.dwojciechowski.model.ServerStatus

interface StatusService {

    companion object {
        fun getInstance(project: Project): StatusService {
            return ServiceManager.getService(project, StatusService::class.java)
        }
    }

    fun getStatus(config: HttpStatusConfig): ServerStatus
    fun getOutputSubject(): Subject<ServerStatus>

}