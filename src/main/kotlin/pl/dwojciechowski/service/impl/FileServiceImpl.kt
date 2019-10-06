package pl.dwojciechowski.service.impl

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import io.grpc.Deadline
import io.grpc.ManagedChannelBuilder
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.proto.FileServiceGrpc
import pl.dwojciechowski.proto.Service
import pl.dwojciechowski.service.FileService
import java.util.concurrent.TimeUnit

class FileServiceImpl(project: Project) : FileService {

    private val config = ServiceManager.getService(project, PluginConfiguration::class.java)

    override fun getDirContent(path: String): Service.FileResponse {
        val channel = ManagedChannelBuilder.forAddress(config.hostname, 4040)
            .usePlaintext()
            .build()

        val pathObj = Service.Path.newBuilder()
            .setName(path)
            .build()

        return FileServiceGrpc.newBlockingStub(channel)
            .withDeadline(Deadline.after(config.timeout.toLong(), TimeUnit.SECONDS))
            .navigate(pathObj)
    }

}
