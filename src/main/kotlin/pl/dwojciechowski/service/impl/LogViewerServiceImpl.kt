package pl.dwojciechowski.service.impl

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import io.rsocket.RSocket
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
        logsObserver: (LogLine) -> Unit,
        logsErrorObserver: (Throwable) -> Unit
    ): Disposable {
        return getLogs(config.logFileLocation, source, logsObserver, logsErrorObserver)
    }

    override fun getCustomLogFile(
        logFileLocation: String,
        logsObserver: (LogLine) -> Unit,
        logsErrorObserver: (Throwable) -> Unit
    ): Disposable {
        return getLogs(logFileLocation, LogFileLocation.Source.CUSTOM, logsObserver, logsErrorObserver)
    }

    private fun getLogs(
        logFileLocation: String,
        source: LogFileLocation.Source,
        logsObserver: (LogLine) -> Unit,
        logsErrorObserver: (Throwable) -> Unit = { println(it) }
    ): Disposable {
        val rSocket = establishConnection()
        val fileLocation = LogFileLocation.newBuilder()
            .setFileLocation(logFileLocation)
            .setLogType(source)
            .build()
        return LogViewerServiceClient(rSocket)
            .getLogs(fileLocation)
            .doOnNext(logsObserver)
            .doOnError(logsErrorObserver)
            .subscribe()
    }

    private fun establishConnection(): RSocket? {
        return RSocketFactory.connect()
            .fragment(1024)
            .transport(TcpClientTransport.create(config.hostname, 4040))
            .start()
            .block()
    }

}