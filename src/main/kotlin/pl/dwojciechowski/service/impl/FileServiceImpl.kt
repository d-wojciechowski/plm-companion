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
    private val emptyResponse = FileResponse.newBuilder()
        .setOs("")
        .setSeparator("")
        .addFileTree(FileMeta.getDefaultInstance())
        .build()

    override fun getDirContent(path: String, fullExpand: Boolean): FileResponse {
        val pathObj = Path.newBuilder()
            .setName(path)
            .setFullExpand(fullExpand)
            .build()

        return FileServiceClient(connector.getConnection())
            .navigate(pathObj)
            .retry(5)
            .block() ?: emptyResponse
    }

}
