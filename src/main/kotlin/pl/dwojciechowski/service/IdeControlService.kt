package pl.dwojciechowski.service

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow

interface IdeControlService {
    companion object {
        fun getInstance(project: Project): IdeControlService {
            return ServiceManager.getService(project, IdeControlService::class.java)
        }
    }

    fun switchToCommandTab()
    fun getToolWindow(id: String?): ToolWindow?
}