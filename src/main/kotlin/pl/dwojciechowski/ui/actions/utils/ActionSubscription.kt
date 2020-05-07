package pl.dwojciechowski.ui.actions.utils

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import io.reactivex.rxjava3.disposables.Disposable
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.model.ServerStatus
import pl.dwojciechowski.service.StatusService

class ActionSubscription {

    private lateinit var subscription: Disposable
    private var project: Project? = null

    fun subscriptionRoutine(e: AnActionEvent, method: (ServerStatus, Boolean) -> Unit) {
        if (project != e.project && e.project != null) {
            project = e.project
            if (this::subscription.isInitialized) {
                subscription.dispose()
            }
            subscription = StatusService.getInstance(project!!).getOutputSubject().subscribe {
                val statCtrld = ServiceManager.getService(project!!, PluginConfiguration::class.java).statusControlled
                method(it, statCtrld)
            }
        }
    }

}