package pl.dwojciechowski.service

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import io.reactivex.rxjava3.subjects.Subject
import pl.dwojciechowski.model.CommandBean
import pl.dwojciechowski.proto.commands.Response

interface WncConnectorService {

    companion object {
        fun getInstance(project: Project): WncConnectorService {
            return ServiceManager.getService(project, WncConnectorService::class.java)
        }
    }

    fun stopWnc(): Response
    fun startWnc(): Response
    fun restartWnc(): Response
    fun xconf(): Response
    fun execCommand(commandBean: CommandBean): Response
    fun executeStreaming(commandBean: CommandBean)

    fun getOutputSubject(): Subject<CommandBean>

}