package pl.dwojciechowski.service.impl

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import pl.dwojciechowski.proto.commands.Response
import pl.dwojciechowski.service.ActionExecutor
import pl.dwojciechowski.ui.PLMPluginNotification
import pl.dwojciechowski.ui.PluginIcons
import javax.swing.JButton

class ActionExecutorImpl(private val project: Project) : ActionExecutor {

    override fun executeAction(actionName: String, action: () -> Response) {
        PLMPluginNotification.notify(project, "Started execution of $actionName", PluginIcons.CONFIRMATION)
        Flowable.fromCallable(action)
            .subscribeOn(Schedulers.newThread())
            .subscribe(
                { handle(it) },
                { showMessageOnUIThread(it) }
            )
    }

    override fun executeAction(button: JButton, action: () -> Response) {
        PLMPluginNotification.notify(project, "Started execution of ${button.name}", PluginIcons.CONFIRMATION)
        button.isEnabled = false
        Flowable.fromCallable(action)
            .subscribeOn(Schedulers.newThread())
            .subscribe(
                { handle(it) },
                { showMessageOnUIThread(it, button) },
                { button.isEnabled = true }
            )
    }

    private fun handle(response: Response) {
        if (response.status == 200) {
            PLMPluginNotification.notify(project, "Action executed successfully", PluginIcons.CONFIRMATION)
        } else {
            PLMPluginNotification.notify(project, "Action FAILED", PluginIcons.ERROR)
        }
    }

    private fun showMessageOnUIThread(error: Throwable, button: JButton? = null) {
        ApplicationManager.getApplication().invokeLater {
            Messages.showMessageDialog(project, error.message, "Connection error", Messages.getErrorIcon())
        }
        button?.isEnabled = true
    }

}