package pl.dwojciechowski.ui.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.ServiceManager
import pl.dwojciechowski.service.RemoteService

class StopWncAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: throw Exception("Project not defined exception")
        val remoteServiceImpl = ServiceManager.getService(project, RemoteService::class.java)
        remoteServiceImpl.stopWnc()
    }

}