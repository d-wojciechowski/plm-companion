package pl.dwojciechowski.service.impl

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.model.CommandBean
import pl.dwojciechowski.proto.commands.CommandServiceClient
import pl.dwojciechowski.proto.commands.Response
import pl.dwojciechowski.proto.commands.Status
import pl.dwojciechowski.service.WncConnectorService

class WncConnectorServiceImpl(private val project: Project) : WncConnectorService {

    private val config = ServiceManager.getService(project, PluginConfiguration::class.java)
    private val commandSubject: Subject<CommandBean> = PublishSubject.create<CommandBean>()

    override fun restartWnc(): Response {
        val response = stopWnc()
        return if (response.status == Status.FINISHED) startWnc() else response
    }

    override fun stopWnc(): Response {
        return execCommand(CommandBean("Windchill Stop", "windchill stop"))
    }

    override fun startWnc(): Response {
        return execCommand(CommandBean("Windchill Start", "windchill start"))
    }

    override fun xconf(): Response {
        return execCommand(CommandBean("xconfmanager -p", "xconfmanager -p"))
    }

    override fun executeStreaming(commandBean: CommandBean) {
        try {
            commandSubject.onNext(commandBean)
            val command = commandBean.getCommand()
            commandBean.status = CommandBean.ExecutionStatus.RUNNING
            commandBean.response.onNext("Started execution of ${command.command} ${command.args}")
            val rSocket = RSocketFactory.connect()
                .resume()
                .transport(TcpClientTransport.create(config.hostname, 4040))
                .start()
                .block()
            val response = CommandServiceClient(rSocket)
                .executeStreaming(command)
                .doOnNext {
                    commandBean.response.onNext(it.message)
                }.doOnError {
                    commandBean.response.onNext(it.message)
                    commandBean.status = CommandBean.ExecutionStatus.STOPPED
                }.doOnComplete {
                    commandBean.status = CommandBean.ExecutionStatus.COMPLETED
                }.subscribe()

        } catch (e: Exception) {
            commandBean.status = CommandBean.ExecutionStatus.STOPPED
            commandBean.response.onNext(e.message)
            ApplicationManager.getApplication().invokeLater {
                Messages.showErrorDialog(project, e.toString(), e.message ?: "")
            }
        }
    }

    override fun execCommand(commandBean: CommandBean): Response {
        try {
            commandSubject.onNext(commandBean)
            val command = commandBean.getCommand()
            commandBean.status = CommandBean.ExecutionStatus.RUNNING
            commandBean.response.onNext("Started execution of ${command.command} ${command.args}")
            val rSocket = RSocketFactory.connect()
                .transport(TcpClientTransport.create(config.hostname, 4040))
                .start()
                .block()
            val response = CommandServiceClient(rSocket)
                .execute(command)
                .block()
            commandBean.response.onNext(response?.message)
            rSocket?.dispose()
            commandBean.status = CommandBean.ExecutionStatus.COMPLETED
            return response
                ?: throw Exception("Could not get response with result of command ${command.command} ${command.args} ")
        } catch (e: Exception) {
            commandBean.status = CommandBean.ExecutionStatus.STOPPED
            commandBean.response.onNext(e.message)
            ApplicationManager.getApplication().invokeLater {
                Messages.showErrorDialog(project, e.toString(), e.message ?: "")
            }
        }
        return Response.newBuilder().setStatus(Status.FINISHED).build()
    }

    override fun getOutputSubject(): Subject<CommandBean> = commandSubject

}
