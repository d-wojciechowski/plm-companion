package pl.dwojciechowski.action

import com.intellij.openapi.project.Project
import pl.dwojciechowski.model.ServerStatus
import pl.dwojciechowski.service.RemoteService

class XConfReloadWncAction : RemoteCommandAction() {

    override fun action(project: Project) = RemoteService.getInstance(project).xconf()

    override fun isEnabled(status: ServerStatus, statusControlled: Boolean) = true

}