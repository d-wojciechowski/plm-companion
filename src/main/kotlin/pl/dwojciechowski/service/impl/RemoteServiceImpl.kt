package pl.dwojciechowski.service.impl

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import io.rsocket.RSocket
import pl.dwojciechowski.configuration.ProjectPluginConfiguration
import pl.dwojciechowski.i18n.PluginBundle.getMessage
import pl.dwojciechowski.model.CommandBean
import pl.dwojciechowski.model.ExecutionStatus
import pl.dwojciechowski.proto.commands.Command
import pl.dwojciechowski.proto.commands.CommandServiceClient
import pl.dwojciechowski.proto.commands.Status
import pl.dwojciechowski.service.ConnectorService
import pl.dwojciechowski.service.IdeControlService
import pl.dwojciechowski.service.RemoteService
import reactor.core.Disposable
import reactor.core.Exceptions
import reactor.util.retry.Retry

class RemoteServiceImpl(private val project: Project) : RemoteService {

    private val config = project.getService(ProjectPluginConfiguration::class.java)
    private val connector = ConnectorService.getInstance(project)
    private val ideService = IdeControlService.getInstance(project)
    private val commandSubject = PublishSubject.create<CommandBean>()

    private val isMacOs: Boolean

    init {
        val osName = System.getProperty("os.name") ?: ""
        isMacOs = osName.contains("mac", true) || osName.contains("darwin", true)
    }

    override fun restartWnc(doFinally: () -> Unit) {
        executeStreaming(
            CommandBean(getMessage("commands.windchill.restart"), "windchill stop && windchill start"),
            doFinally
        )
    }

    override fun stopWnc(doFinally: () -> Unit) {
        return executeStreaming(CommandBean(getMessage("commands.windchill.stop"), "windchill stop"), doFinally)
    }

    override fun startWnc(doFinally: () -> Unit) {
        return executeStreaming(CommandBean(getMessage("commands.windchill.start"), "windchill start"), doFinally)
    }

    override fun xconf(doFinally: () -> Unit) {
        return executeStreaming(CommandBean(getMessage("commands.windchill.xconfReload"), "xconfmanager -p"), doFinally)
    }

    override fun executeStreaming(commandBean: CommandBean, doFinally: () -> Unit) {
        try {
            if (!config.autoOpenCommandPane) {
                ideService.initCommandTab()
            }

            val command = commandBean.getCommand()
            commandBean.status = ExecutionStatus.RUNNING
            commandBean.response.onNext(getMessage("execution.process.start", commandBean))

            val rSocket = connector.getConnection()
            commandBean.actualSubscription = rSocket.executeStreamingCall(command, commandBean, doFinally)
            commandSubject.onNext(commandBean)
        } catch (e: Exception) {
            val message =
                getMessage("execution.process.exception", "${commandBean}\n${Exceptions.unwrap(e).message ?: ""}")
            commandBean.status = ExecutionStatus.STOPPED
            commandBean.response.onNext(e.message)
            doFinally()
            ApplicationManager.getApplication().invokeLater {
                Messages.showErrorDialog(project, message, getMessage("ui.dialog.error.connection"))
            }
        }
    }

    private fun RSocket.executeStreamingCall(
        command: Command,
        commandBean: CommandBean,
        doFinally: () -> Unit
    ): Disposable {
        return CommandServiceClient(this)
            .executeStreaming(command)
            .retryWhen(Retry.maxInARow(0))
            .doOnNext {
                if (isMacOs) {
                    commandBean.response.onNext(it.message.replace("\u0000", ""))
                } else {
                    commandBean.response.onNext(it.message)
                }
                if (it.status == Status.FAILED) {
                    commandBean.status = ExecutionStatus.STOPPED
                }
            }.doOnError {
                var message = Exceptions.unwrap(it).message ?: ""
                if (message.contains("Retries exhausted")) {
                    message = message.replace("0", connector.maxAttempts().toString())
                }
                commandBean.response.onNext(message)
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
