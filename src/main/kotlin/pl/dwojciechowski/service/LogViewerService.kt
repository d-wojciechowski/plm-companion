package pl.dwojciechowski.service

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import pl.dwojciechowski.proto.files.LogFileLocation
import pl.dwojciechowski.proto.files.LogLine
import reactor.core.Disposable

interface LogViewerService {

    companion object {
        fun getInstance(project: Project): LogViewerService {
            return ServiceManager.getService(project, LogViewerService::class.java)
        }
    }

    fun getLogFile(
        source: LogFileLocation.Source,
        logsObserver: (LogLine) -> Unit,
        logsErrorObserver: (Throwable) -> Unit = { println(it) }
    ): Disposable

    fun getCustomLogFile(
        logFileLocation: String,
        logsObserver: (LogLine) -> Unit,
        logsErrorObserver: (Throwable) -> Unit =  { println(it) }
    ): Disposable

}