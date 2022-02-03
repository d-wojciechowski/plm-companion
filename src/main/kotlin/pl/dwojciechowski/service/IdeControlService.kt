package pl.dwojciechowski.service

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentManager

interface IdeControlService {

    companion object {
        fun getInstance(project: Project): IdeControlService {
            return project.getService(IdeControlService::class.java)
        }
    }

    fun switchToCommandTab()
    fun initCommandTab()
    fun getToolWindow(id: String?): ToolWindow?
    fun withCommandTab(doWith: (ToolWindow, ContentManager, Content) -> Unit)
    fun withAutoOpen(action: () -> Unit)

}