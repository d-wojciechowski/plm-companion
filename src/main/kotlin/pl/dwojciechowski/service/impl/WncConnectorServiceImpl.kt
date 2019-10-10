package pl.dwojciechowski.service.impl

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import io.grpc.Deadline
import io.grpc.ManagedChannelBuilder
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.proto.CommandServiceGrpc
import pl.dwojciechowski.proto.Service
import pl.dwojciechowski.service.WncConnectorService
import java.util.concurrent.TimeUnit

class WncConnectorServiceImpl(private val project: Project) : WncConnectorService {

    private val config = ServiceManager.getService(project, PluginConfiguration::class.java)

    override fun restartWnc(): Service.Response {
        val response = stopWnc()
        return if (response.status == 200) startWnc() else response
    }

    override fun stopWnc(): Service.Response {
        return execCommand(
            Service.Command.newBuilder()
                .setCommand("windchill")
                .setArgs("stop")
                .build()
        )
    }

    override fun startWnc(): Service.Response {
        return execCommand(
            Service.Command.newBuilder()
                .setCommand("windchill")
                .setArgs("start")
                .build()
        )
    }

    override fun xconf(): Service.Response {
        return execCommand(
            Service.Command.newBuilder()
                .setCommand("xconfmanager")
                .setArgs("-p")
                .build()
        )
    }

    private fun execCommand(command: Service.Command): Service.Response {
        val channel = ManagedChannelBuilder.forAddress(config.hostname, 4040)
            .usePlaintext()
            .build()

        val stub = CommandServiceGrpc.newBlockingStub(channel)
            .withDeadline(Deadline.after(config.timeout.toLong(), TimeUnit.SECONDS))
        val response = stub.execute(command)
        channel.shutdown()
        return response
    }

}
