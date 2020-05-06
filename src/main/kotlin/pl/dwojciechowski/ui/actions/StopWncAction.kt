package pl.dwojciechowski.ui.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import pl.dwojciechowski.model.ServerStatus
import pl.dwojciechowski.service.RemoteService
import pl.dwojciechowski.ui.actions.utils.ActionSubscription

class StopWncAction : DumbAwareAction() {

    private val actionSubscription = ActionSubscription()

    private val enabledStatusList = listOf(ServerStatus.RUNNING)
    private var isEnabled = false

    override fun update(e: AnActionEvent) {
        actionSubscription.subscriptionRoutine(e) {
            isEnabled = it == ServerStatus.NOT_SCANNING || enabledStatusList.contains(it)
        }
        e.presentation.isEnabled = isEnabled
    }

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let { RemoteService.getInstance(it).stopWnc() }
    }

}