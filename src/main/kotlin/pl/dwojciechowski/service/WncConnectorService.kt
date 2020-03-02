package pl.dwojciechowski.service

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import io.reactivex.rxjava3.subjects.Subject
import pl.dwojciechowski.model.CommandBean

interface WncConnectorService {

    companion object {
        fun getInstance(project: Project): WncConnectorService {
            return ServiceManager.getService(project, WncConnectorService::class.java)
        }
    }

    fun stopWnc()
    fun startWnc()
    fun restartWnc()
    fun xconf()
    fun executeStreaming(commandBean: CommandBean)

    fun getOutputSubject(): Subject<CommandBean>

}