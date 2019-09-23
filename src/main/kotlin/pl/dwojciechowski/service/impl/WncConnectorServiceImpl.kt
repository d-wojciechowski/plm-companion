package pl.dwojciechowski.service.impl

import io.grpc.Deadline
import io.grpc.ManagedChannelBuilder
import pl.dwojciechowski.proto.CommandServiceGrpc
import pl.dwojciechowski.proto.Service
import pl.dwojciechowski.service.WncConnectorService
import java.util.concurrent.TimeUnit

class WncConnectorServiceImpl() : WncConnectorService {

    override fun restartWnc(hostname: String) {
        stopWnc(hostname)
        startWnc(hostname)
    }

    override fun stopWnc(hostname: String) {
        execCommand(
            hostname,
            Service.Command.newBuilder()
                .setCommand("windchill")
                .setArgs("stop")
                .build()
        )
    }

    override fun startWnc(hostname: String) {
        execCommand(
            hostname,
            Service.Command.newBuilder()
                .setCommand("windchill")
                .setArgs("start")
                .build()
        )
    }

    private fun execCommand(hostname: String, command: Service.Command): Service.Response? {
        val channel = ManagedChannelBuilder.forAddress(hostname, 4040)
            .usePlaintext()
            .build()

        val stub = CommandServiceGrpc.newBlockingStub(channel).withDeadline(Deadline.after(3, TimeUnit.SECONDS))
        val response = stub.execute(command)
        channel.shutdown()
        return response
    }

}