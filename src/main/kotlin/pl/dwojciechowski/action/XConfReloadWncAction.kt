package pl.dwojciechowski.action

import com.intellij.openapi.project.Project
import pl.dwojciechowski.model.ServerStatus
import pl.dwojciechowski.service.RemoteService

class XConfReloadWncAction : RemoteCommandAction() {

    private val enabledStatusList = listOf(ServerStatus.RUNNING)

    override fun action(project: Project) = RemoteService.getInstance(project).xconf()

    override fun isEnabled(status: ServerStatus, statusControlled: Boolean): Boolean {
        return !statusControlled || status == ServerStatus.NOT_SCANNING || enabledStatusList.contains(status)
    }

}