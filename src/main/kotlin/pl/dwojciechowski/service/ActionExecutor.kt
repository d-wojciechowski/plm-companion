package pl.dwojciechowski.service

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import pl.dwojciechowski.proto.commands.Response

import javax.swing.JButton

interface ActionExecutor {

    companion object {
        fun getInstance(project: Project): ActionExecutor {
            return ServiceManager.getService(project, ActionExecutor::class.java)
        }
    }

    fun executeAction(actionName: String, action: () -> Response)
    fun executeAction(button: JButton, action: () -> Response)

}
