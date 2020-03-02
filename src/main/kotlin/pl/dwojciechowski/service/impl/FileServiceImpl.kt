package pl.dwojciechowski.service.impl

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import io.rsocket.RSocket
import io.rsocket.RSocketFactory
import io.rsocket.transport.netty.client.TcpClientTransport
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.proto.files.FileResponse
import pl.dwojciechowski.proto.files.FileServiceClient
import pl.dwojciechowski.proto.files.Path
import pl.dwojciechowski.service.FileService

class FileServiceImpl(project: Project) : FileService {

    private val config = ServiceManager.getService(project, PluginConfiguration::class.java)

    @Throws(Exception::class)
    override fun getDirContent(path: String, fullExpand: Boolean): FileResponse {
        val rSocket = establishConnection()
        val pathObj = Path.newBuilder()
            .setName(path)
            .setFullExpand(fullExpand)
            .build()
        val response = FileServiceClient(rSocket)
            .navigate(pathObj)
            .block()
        rSocket?.dispose()
        return response ?: throw Exception("Could not get response with directory content")
    }

    private fun establishConnection(): RSocket? {
        return RSocketFactory.connect()
            .fragment(1024)
            .transport(TcpClientTransport.create(config.hostname, 4040))
            .start()
            .block()
    }

}
