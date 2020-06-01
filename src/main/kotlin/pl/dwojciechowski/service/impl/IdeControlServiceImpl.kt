package pl.dwojciechowski.service.impl

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentManager
import pl.dwojciechowski.service.IdeControlService

class IdeControlServiceImpl(private val project: Project) : IdeControlService {

    override fun switchToCommandTab() {
        withCommandTab { toolWindow, contentManager, content ->
            toolWindow.show()
            contentManager.setSelectedContent(content, true, false)
        }
    }

    override fun withCommandTab(doWith: (ToolWindow, ContentManager, Content) -> Unit) {
        getToolWindow("PLM Companion Log")?.let { toolWindow ->
            val contentManager = toolWindow.contentManager
            contentManager.getContent(2)?.let { doWith(toolWindow, contentManager, it) }
        }
    }

    override fun getToolWindow(id: String?) = ToolWindowManager.getInstance(project).getToolWindow(id)

}