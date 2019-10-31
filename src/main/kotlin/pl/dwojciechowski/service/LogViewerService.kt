package pl.dwojciechowski.service

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import io.grpc.ManagedChannel
import io.grpc.stub.StreamObserver
import pl.dwojciechowski.proto.Service

interface LogViewerService {

    companion object {
        fun getInstance(project: Project): LogViewerService {
            return ServiceManager.getService(project, LogViewerService::class.java)
        }
    }

    fun getLogFile(
        source: Service.LogFileLocation.Source,
        logsObserver: StreamObserver<Service.LogLine>
    ): ManagedChannel

    fun getCustomLogFile(
        logFileLocation: String,
        logsObserver: StreamObserver<Service.LogLine>
    ): ManagedChannel

}