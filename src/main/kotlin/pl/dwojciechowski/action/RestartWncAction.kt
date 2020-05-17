package pl.dwojciechowski.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import pl.dwojciechowski.action.utils.ActionSubscription
import pl.dwojciechowski.model.ServerStatus
import pl.dwojciechowski.service.RemoteService

class RestartWncAction : DumbAwareAction() {

    private val actionSubscription = ActionSubscription()

    private val enabledStatusList = listOf(ServerStatus.RUNNING)
    private var isEnabled = false

    override fun update(e: AnActionEvent) {
        actionSubscription.subscriptionRoutine(e) { status, statusControlled ->
            isEnabled = !statusControlled || status == ServerStatus.NOT_SCANNING || enabledStatusList.contains(status)
        }
        e.presentation.isEnabled = isEnabled
    }

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let { RemoteService.getInstance(it).restartWnc() }
    }

}