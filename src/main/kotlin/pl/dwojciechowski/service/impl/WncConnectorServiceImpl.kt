package pl.dwojciechowski.service.impl

import io.grpc.Deadline
import io.grpc.ManagedChannelBuilder
import pl.dwojciechowski.model.CommandConfig
import pl.dwojciechowski.proto.CommandServiceGrpc
import pl.dwojciechowski.proto.Service
import pl.dwojciechowski.service.WncConnectorService
import java.util.concurrent.TimeUnit

class WncConnectorServiceImpl : WncConnectorService {

    override fun restartWnc(cfg: CommandConfig): Service.Response {
        val response = stopWnc(cfg)
        return if(response.status == 200) startWnc(cfg) else response
    }

    override fun stopWnc(cfg: CommandConfig): Service.Response {
        return execCommand(
            cfg,
            Service.Command.newBuilder()
                .setCommand("windchill")
                .setArgs("stop")
                .build()
        )
    }

    override fun startWnc(cfg: CommandConfig): Service.Response {
        return execCommand(
            cfg,
            Service.Command.newBuilder()
                .setCommand("windchill")
                .setArgs("start")
                .build()
        )
    }

    override fun xconf(cfg: CommandConfig): Service.Response {
        return execCommand(
            cfg,
            Service.Command.newBuilder()
                .setCommand("xconfmanager")
                .setArgs("-p")
                .build()
        )
    }

    private fun execCommand(cfg: CommandConfig, command: Service.Command): Service.Response {
        val channel = ManagedChannelBuilder.forAddress(cfg.hostname, 4040)
            .usePlaintext()
            .build()

        val stub = CommandServiceGrpc.newBlockingStub(channel)
            .withDeadline(Deadline.after(cfg.timeout.toLong(), TimeUnit.SECONDS))
        val response = stub.execute(command)
        channel.shutdown()
        return response
    }

}
