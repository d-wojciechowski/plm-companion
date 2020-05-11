package pl.dwojciechowski.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import pl.dwojciechowski.actions.utils.ActionSubscription
import pl.dwojciechowski.model.ServerStatus
import pl.dwojciechowski.service.RemoteService

class StartWncAction : DumbAwareAction() {

    private val actionSubscription = ActionSubscription()

    private val disabledStatusList = listOf(ServerStatus.RUNNING)
    private var isEnabled = false

    override fun update(e: AnActionEvent) {
        actionSubscription.subscriptionRoutine(e) { status, statusControlled ->
            isEnabled = !statusControlled || status == ServerStatus.NOT_SCANNING || !disabledStatusList.contains(status)
        }
        e.presentation.isEnabled = isEnabled
    }

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let { RemoteService.getInstance(it).startWnc() }
    }

}