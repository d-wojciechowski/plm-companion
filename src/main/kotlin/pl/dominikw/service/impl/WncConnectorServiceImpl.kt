package pl.dominikw.service.impl

import io.grpc.ManagedChannelBuilder
import pl.dominikw.proto.CommandServiceGrpc
import pl.dominikw.proto.Service
import pl.dominikw.service.WncConnectorService

class WncConnectorServiceImpl() : WncConnectorService {

    override fun restartWnc(hostname: String) {
        stopWnc(hostname)
        startWnc(hostname)
    }

    override fun stopWnc(hostname: String) {
        execCommand( hostname,
            Service.Command.newBuilder()
                .setCommand("windchill")
                .setArgs("stop")
                .build()
        )
    }

    override fun startWnc(hostname: String) {
        execCommand(hostname,
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

        val stub = CommandServiceGrpc.newBlockingStub(channel)
        val response = stub.execute(command)
        channel.shutdown()
        return response
    }

}