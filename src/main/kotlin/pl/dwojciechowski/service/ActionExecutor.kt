package pl.dwojciechowski.service

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import pl.dwojciechowski.proto.Service
import javax.swing.JButton

interface ActionExecutor {

    companion object {
        fun getInstance(project: Project): ActionExecutor {
            return ServiceManager.getService(project, ActionExecutor::class.java)
        }
    }

    fun executeAction(actionName: String, action: () -> Service.Response)
    fun executeAction(button: JButton, action: () -> Service.Response)

}
