package pl.dwojciechowski.service.impl

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import pl.dwojciechowski.proto.files.FileMeta
import pl.dwojciechowski.proto.files.FileResponse
import pl.dwojciechowski.proto.files.FileServiceClient
import pl.dwojciechowski.proto.files.Path
import pl.dwojciechowski.service.ConnectorService
import pl.dwojciechowski.service.FileService

class FileServiceImpl(project: Project) : FileService {

    private val connector = ServiceManager.getService(project, ConnectorService::class.java)

    override fun getDirContent(path: String, fullExpand: Boolean): FileResponse {
        val rSocket = connector.establishConnection()
        val pathObj = Path.newBuilder()
            .setName(path)
            .setFullExpand(fullExpand)
            .build()
        val response = FileServiceClient(rSocket)
            .navigate(pathObj)
            .block()
        rSocket?.dispose()
        return response ?: FileResponse.newBuilder()
            .setOs("")
            .setSeparator("")
            .addFileTree(FileMeta.getDefaultInstance())
            .build()
    }

}
