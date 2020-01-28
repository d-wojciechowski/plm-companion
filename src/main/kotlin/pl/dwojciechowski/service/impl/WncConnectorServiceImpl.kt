package pl.dwojciechowski.service.impl

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.proto.commands.Command
import pl.dwojciechowski.proto.commands.CommandServiceClient
import pl.dwojciechowski.proto.commands.Response
import pl.dwojciechowski.service.WncConnectorService

class WncConnectorServiceImpl(private val project: Project) : WncConnectorService {

    private val config = ServiceManager.getService(project, PluginConfiguration::class.java)

    override fun restartWnc(): Response {
        val response = stopWnc()
        return if (response.status == 200) startWnc() else response
    }

    override fun stopWnc(): Response {
        return execCommand(
            Command.newBuilder()
                .setCommand("windchill")
                .setArgs("stop")
                .build()
        )
    }

    override fun startWnc(): Response {
        return execCommand(
            Command.newBuilder()
                .setCommand("windchill")
                .setArgs("start")
                .build()
        )
    }

    override fun xconf(): Response {
        return execCommand(
            Command.newBuilder()
                .setCommand("xconfmanager")
                .setArgs("-p")
                .build()
        )
    }

    override fun execCommand(command: Command): Response {
        try {
            val rSocket = RSocketFactory.connect()
                .transport(TcpClientTransport.create(config.hostname, 4040))
                .start()
                .block()
            val response = CommandServiceClient(rSocket)
                .execute(command)
                .block()
            rSocket?.dispose()
            return response ?: throw Exception("Could not get response with result of command")
        } catch (e: Exception) {
            ApplicationManager.getApplication().invokeLater {
                Messages.showErrorDialog(project, e.toString(), e.message ?: "")
            }
        }
        return Response.newBuilder().setStatus(500).build()
    }


}
