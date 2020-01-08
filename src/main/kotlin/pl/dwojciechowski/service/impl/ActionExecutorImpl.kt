package pl.dwojciechowski.service.impl

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import io.grpc.StatusRuntimeException
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import pl.dwojciechowski.proto.Service
import pl.dwojciechowski.service.ActionExecutor
import pl.dwojciechowski.ui.PluginIcons
import pl.dwojciechowski.ui.WindchillNotification
import javax.swing.JButton

class ActionExecutorImpl(private val project: Project) : ActionExecutor {

    override fun executeAction(actionName: String, action: () -> Service.Response) {
        WindchillNotification.notify(project, "Started execution of $actionName", PluginIcons.CONFIRMATION)
        Flowable.fromCallable(action)
            .subscribeOn(Schedulers.newThread())
            .subscribe(
                { handle(it) },
                { showMessageOnUIThread(it) }
            )
    }

    override fun executeAction(button: JButton, action: () -> Service.Response) {
        WindchillNotification.notify(project, "Started execution of ${button.name}", PluginIcons.CONFIRMATION)
        button.isEnabled = false
        Flowable.fromCallable(action)
            .subscribeOn(Schedulers.newThread())
            .subscribe(
                { handle(it) },
                { showMessageOnUIThread(it, button) },
                { button.isEnabled = true }
            )
    }

    private fun handle(response: Service.Response) {
        if (response.status == 200) {
            WindchillNotification.notify(project, "Action executed successfully", PluginIcons.CONFIRMATION)
        } else {
            WindchillNotification.notify(project, "Action FAILED", PluginIcons.ERROR)
        }
    }

    private fun showMessageOnUIThread(error: Throwable, button: JButton? = null) {
        ApplicationManager.getApplication().invokeLater {
            if (error is StatusRuntimeException) {
                Messages.showMessageDialog(
                    project, error.status.description, "Action execution error", Messages.getErrorIcon()
                )
            } else {
                Messages.showMessageDialog(project, error.message, "Connection error", Messages.getErrorIcon())
            }
        }
        button?.isEnabled = true
    }

}