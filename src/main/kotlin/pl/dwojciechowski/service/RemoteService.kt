package pl.dwojciechowski.service

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import io.reactivex.rxjava3.subjects.Subject
import pl.dwojciechowski.model.CommandBean

interface RemoteService {

    companion object {
        fun getInstance(project: Project): RemoteService {
            return ServiceManager.getService(project, RemoteService::class.java)
        }
    }

    fun stopWnc(doFinally: () -> Unit = {})
    fun startWnc(doFinally: () -> Unit = {})
    fun restartWnc(doFinally: () -> Unit = {})
    fun xconf(doFinally: () -> Unit = {})
    fun executeStreaming(commandBean: CommandBean, doFinally: () -> Unit = {})

    fun getOutputSubject(): Subject<CommandBean>

}