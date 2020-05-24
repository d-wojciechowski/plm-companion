package pl.dwojciechowski.service.impl

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import io.rsocket.RSocket
import pl.dwojciechowski.model.CommandBean
import pl.dwojciechowski.model.ExecutionStatus
import pl.dwojciechowski.proto.commands.Command
import pl.dwojciechowski.proto.commands.CommandServiceClient
import pl.dwojciechowski.proto.commands.Status
import pl.dwojciechowski.service.ConnectorService
import pl.dwojciechowski.service.RemoteService
import reactor.core.Exceptions

class RemoteServiceImpl(private val project: Project) : RemoteService {

    private val connector = ServiceManager.getService(project, ConnectorService::class.java)
    private val commandSubject: Subject<CommandBean> = PublishSubject.create<CommandBean>()

    override fun restartWnc(doFinally: () -> Unit) {
        executeStreaming(CommandBean("Windchill Restart", "windchill stop && windchill start"), doFinally)
    }

    override fun stopWnc(doFinally: () -> Unit) {
        return executeStreaming(CommandBean("Windchill Stop", "windchill stop"), doFinally)
    }

    override fun startWnc(doFinally: () -> Unit) {
        return executeStreaming(CommandBean("Windchill Start", "windchill start"), doFinally)
    }

    override fun xconf(doFinally: () -> Unit) {
        return executeStreaming(CommandBean("Xconfmanager Reload", "xconfmanager -p"), doFinally)
    }

    override fun executeStreaming(commandBean: CommandBean, doFinally: () -> Unit) {
        try {
            val command = commandBean.getCommand()
            commandBean.status = ExecutionStatus.RUNNING
            commandBean.response.onNext("Started execution of $commandBean")
            val rSocket = connector.getConnection()
            rSocket.executeStreamingCall(command, commandBean, doFinally)
            commandBean.actualSubscription = rSocket
            commandSubject.onNext(commandBean)
        } catch (e: Exception) {
            commandBean.status = ExecutionStatus.STOPPED
            commandBean.response.onNext(e.message)
            doFinally()
            ApplicationManager.getApplication().invokeLater {
                Messages.showErrorDialog(project, Exceptions.unwrap(e).message ?: "", "Connection exception")
            }
        }
    }

    private fun RSocket.executeStreamingCall(command: Command, commandBean: CommandBean, doFinally: () -> Unit) {
        CommandServiceClient(this)
            .executeStreaming(command)
            .doOnNext {
                commandBean.response.onNext(it.message)
                if (it.status == Status.FAILED) {
                    commandBean.status = ExecutionStatus.STOPPED
                }
            }.doOnError {
                commandBean.response.onNext(it.message)
                commandBean.status = ExecutionStatus.STOPPED
            }.doOnComplete {
                if (commandBean.status != ExecutionStatus.STOPPED) {
                    commandBean.status = ExecutionStatus.COMPLETED
                }
            }.doFinally {
                doFinally()
            }.subscribe()
    }

    override fun getOutputSubject(): Subject<CommandBean> = commandSubject

}
