package pl.dwojciechowski.service.impl

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import io.grpc.StatusRuntimeException
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import pl.dwojciechowski.proto.Service
import pl.dwojciechowski.service.ActionExecutor
import pl.dwojciechowski.ui.PluginIcons
import pl.dwojciechowski.ui.WindchillNotification
import javax.swing.JButton

class ActionExecutorImpl(private val project: Project) : ActionExecutor {

    override fun executeAction(actionName: String, action: () -> Service.Response) {
        WindchillNotification.createNotification(project, "Started execution of $actionName", PluginIcons.OK)
        Flowable.fromCallable(action)
            .subscribeOn(Schedulers.newThread())
            .subscribe(
                { handle(it) },
                { showMessageOnUIThread(it) }
            )
    }

    override fun executeAction(button: JButton, action: () -> Service.Response) {
        WindchillNotification.createNotification(project, "Started execution of ${button.name}", PluginIcons.OK)
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
            WindchillNotification.createNotification(
                project,
                "Action executed successfully",
                PluginIcons.OK
            )
        } else {
            WindchillNotification.createNotification(project, "Action FAILED", PluginIcons.KO)
        }
    }

    private fun showMessageOnUIThread(error: Throwable, button: JButton? = null) {
        ApplicationManager.getApplication().invokeLater {
            if (error is StatusRuntimeException) {
                Messages.showMessageDialog(
                    project, error.status.description, "Action execution error", Messages.getErrorIcon()
                )
            } else {
                Messages.showMessageDialog(
                    project,
                    error.message,
                    "Connection error",
                    Messages.getErrorIcon()
                )
            }
        }
        button?.isEnabled = true
    }

}