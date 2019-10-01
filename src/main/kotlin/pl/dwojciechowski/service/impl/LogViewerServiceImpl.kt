package pl.dwojciechowski.service.impl

import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.proto.LogViewerServiceGrpc
import pl.dwojciechowski.proto.Service
import pl.dwojciechowski.service.LogViewerService

class LogViewerServiceImpl : LogViewerService {
    override fun getLogFile(config: PluginConfiguration, logsObserver: StreamObserver<Service.LogLine>) {
        val channel = ManagedChannelBuilder.forAddress(config.hostname, 4040)
            .usePlaintext()
            .build()

        val fileLocation = Service.LogFileLocation.newBuilder()
            .setFileLocation(config.logFileLocation)
            .build()

        LogViewerServiceGrpc.newStub(channel)
            .getLogs(fileLocation, logsObserver)
    }

}