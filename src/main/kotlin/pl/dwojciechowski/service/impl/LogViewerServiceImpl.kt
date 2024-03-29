package pl.dwojciechowski.service.impl

import com.intellij.openapi.project.Project
import pl.dwojciechowski.configuration.ProjectPluginConfiguration
import pl.dwojciechowski.proto.files.LogFileLocation
import pl.dwojciechowski.proto.files.LogLine
import pl.dwojciechowski.proto.files.LogViewerServiceClient
import pl.dwojciechowski.service.ConnectorService
import pl.dwojciechowski.service.LogViewerService
import reactor.core.Disposable

class LogViewerServiceImpl(project: Project) : LogViewerService {

    private val config = project.getService(ProjectPluginConfiguration::class.java)
    private val connector = project.getService(ConnectorService::class.java)

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
        val fileLocation = LogFileLocation.newBuilder()
            .setFileLocation(logFileLocation)
            .setLogType(source)
            .build()
        return LogViewerServiceClient(connector.getConnection())
            .getLogs(fileLocation)
            .retry(5)
            .doOnNext(logsObserver)
            .doOnError(logsErrorObserver)
            .subscribe()
    }

}