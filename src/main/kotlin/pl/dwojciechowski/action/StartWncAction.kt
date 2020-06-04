package pl.dwojciechowski.action

import com.intellij.openapi.project.Project
import pl.dwojciechowski.model.ServerStatus
import pl.dwojciechowski.service.RemoteService

class StartWncAction : RemoteCommandAction() {

    private val disabledStatusList = listOf(ServerStatus.RUNNING)

    override fun action(project: Project) = RemoteService.getInstance(project).startWnc()

    override fun isEnabled(status: ServerStatus, statusControlled: Boolean): Boolean {
        return !statusControlled || status == ServerStatus.NOT_SCANNING || !disabledStatusList.contains(status)
    }

}