package pl.dwojciechowski.service.impl

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.proto.files.LogFileLocation
import pl.dwojciechowski.proto.files.LogLine
import pl.dwojciechowski.proto.files.LogViewerServiceClient
import pl.dwojciechowski.service.LogViewerService
import reactor.core.Disposable

class LogViewerServiceImpl(project: Project) : LogViewerService {
    private val config = ServiceManager.getService(project, PluginConfiguration::class.java)

    override fun getLogFile(
        source: LogFileLocation.Source,
        logsObserver: (LogLine) -> Unit
    ): Disposable {
        return getLogs(config.logFileLocation, logsObserver, source)
    }

    override fun getCustomLogFile(
        logFileLocation: String,
        logsObserver: (LogLine) -> Unit
    ): Disposable {
        return getLogs(logFileLocation, logsObserver, LogFileLocation.Source.CUSTOM)
    }

    private fun getLogs(
        logFileLocation: String,
        logsObserver: (LogLine) -> Unit,
        source: LogFileLocation.Source
    ): Disposable {
        val rSocket = RSocketFactory.connect()
            .fragment(1024)
            .transport(TcpClientTransport.create(config.hostname, 4040))
            .start().block()
        val fileLocation = LogFileLocation.newBuilder()
            .setFileLocation(logFileLocation)
            .setLogType(source)
            .build()
        return LogViewerServiceClient(rSocket)
            .getLogs(fileLocation)
            .doOnNext(logsObserver)
            .subscribe()
    }

}