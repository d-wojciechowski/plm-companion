package pl.dwojciechowski.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.dwojciechowski.action.utils.ActionSubscription
import pl.dwojciechowski.model.ServerStatus

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
        GlobalScope.launch {
            e.project?.let {
                action(it)
            }
        }
    }

}