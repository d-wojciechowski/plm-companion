package pl.dwojciechowski.service.impl

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import pl.dwojciechowski.service.IdeControlService

class IdeControlServiceImpl(private val project: Project) : IdeControlService {

    override fun switchToCommandTab() {
        getToolWindow("PLM Companion Log")?.let { toolWindow ->
            val contentManager = toolWindow.contentManager
            contentManager.getContent(2)?.let { content ->
                toolWindow.show()
                contentManager.setSelectedContent(content, true, false)
            }
        }
    }

    override fun getToolWindow(id: String?) = ToolWindowManager.getInstance(project).getToolWindow(id)

}