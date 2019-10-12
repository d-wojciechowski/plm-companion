package pl.dwojciechowski.service.impl

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.proto.LogViewerServiceGrpc
import pl.dwojciechowski.proto.Service
import pl.dwojciechowski.service.LogViewerService

class LogViewerServiceImpl(private val project: Project) : LogViewerService {

    private val config = ServiceManager.getService(project, PluginConfiguration::class.java)

    override fun getLogFile(
        source: Service.LogFileLocation.Source,
        logsObserver: StreamObserver<Service.LogLine>
    ): ManagedChannel {
        val channel = ManagedChannelBuilder.forAddress(config.hostname, 4040)
            .usePlaintext()
            .build()

        val fileLocation = Service.LogFileLocation.newBuilder()
            .setFileLocation(config.logFileLocation)
            .setLogType(source)
            .build()

        LogViewerServiceGrpc.newStub(channel)
            .getLogs(fileLocation, logsObserver)
        return channel
    }

}