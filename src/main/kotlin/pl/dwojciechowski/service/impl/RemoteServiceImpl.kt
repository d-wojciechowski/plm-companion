package pl.dwojciechowski.service.impl

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import io.rsocket.RSocket
import pl.dwojciechowski.model.CommandBean
import pl.dwojciechowski.proto.commands.Command
import pl.dwojciechowski.proto.commands.CommandServiceClient
import pl.dwojciechowski.proto.commands.Status
import pl.dwojciechowski.service.ConnectorService
import pl.dwojciechowski.service.RemoteService
import pl.dwojciechowski.ui.PLMPluginNotification
import pl.dwojciechowski.ui.PluginIcons
import reactor.core.Exceptions

class RemoteServiceImpl(private val project: Project) : RemoteService {

    private val connector = ServiceManager.getService(project, ConnectorService::class.java)
    private val commandSubject: Subject<CommandBean> = PublishSubject.create<CommandBean>()

    override fun restartWnc() {
        executeStreaming(CommandBean("Windchill Restart", "windchill stop && windchill start"))
    }

    override fun stopWnc() {
        return executeStreaming(CommandBean("Windchill Stop", "windchill stop"))
    }

    override fun startWnc() {
        return executeStreaming(CommandBean("Windchill Start", "windchill start"))
    }

    override fun xconf() {
        return executeStreaming(CommandBean("Xconfmanager Reload", "xconfmanager -p"))
    }

    override fun executeStreaming(commandBean: CommandBean) {
        try {
            val command = commandBean.getCommand()
            commandBean.status = CommandBean.ExecutionStatus.RUNNING
            PLMPluginNotification.notify(project, "$commandBean started", PluginIcons.CONFIRMATION)
            commandBean.response.onNext("Started execution of $commandBean")
            val rSocket = connector.establishConnection()
            rSocket.executeStreamingCall(command, commandBean)
            commandBean.actualSubscription = rSocket ?: commandBean.actualSubscription
            commandSubject.onNext(commandBean)
        } catch (e: Exception) {
            commandBean.status = CommandBean.ExecutionStatus.STOPPED
            commandBean.response.onNext(e.message)
            ApplicationManager.getApplication().invokeLater {
                Messages.showErrorDialog(project, Exceptions.unwrap(e).message ?: "", "Connection exception")
            }
        }
    }

    private fun RSocket?.executeStreamingCall(command: Command, commandBean: CommandBean) {
        CommandServiceClient(this)
            .executeStreaming(command)
            .doOnNext {
                commandBean.response.onNext(it.message)
                if (it.status == Status.FAILED) {
                    commandBean.status = CommandBean.ExecutionStatus.STOPPED
                }
            }.doOnError {
                commandBean.response.onNext(it.message)
                PLMPluginNotification.notify(project, "Error on $commandBean", PluginIcons.ERROR)
                commandBean.status = CommandBean.ExecutionStatus.STOPPED
            }.doOnComplete {
                if (commandBean.status != CommandBean.ExecutionStatus.STOPPED) {
                    commandBean.status = CommandBean.ExecutionStatus.COMPLETED
                    PLMPluginNotification.notify(project, "$commandBean completed", PluginIcons.CONFIRMATION)
                } else {
                    PLMPluginNotification.notify(project, "Error on $commandBean", PluginIcons.ERROR)
                }
            }.subscribe()
    }

    override fun getOutputSubject(): Subject<CommandBean> = commandSubject

}
