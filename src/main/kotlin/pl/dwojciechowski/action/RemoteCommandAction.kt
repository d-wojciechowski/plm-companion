package pl.dwojciechowski.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import pl.dwojciechowski.action.utils.ActionSubscription
import pl.dwojciechowski.model.ServerStatus
import pl.dwojciechowski.service.IdeControlService

abstract class RemoteCommandAction : DumbAwareAction() {

    private val actionSubscription = ActionSubscription()
    private var isEnabled = false

    abstract fun action(project: Project)
    abstract fun isEnabled(status: ServerStatus, statusControlled: Boolean): Boolean

    override fun update(e: AnActionEvent) {
        actionSubscription.subscriptionRoutine(e) { status, statusControlled ->
            isEnabled = isEnabled(status, statusControlled)
        }
        e.presentation.isEnabled = isEnabled
    }

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let {
            ApplicationManager.getApplication().invokeLater {
                ServiceManager.getService(it, IdeControlService::class.java).withAutoOpen {
                    action(it)
                }
            }
        }
    }

}