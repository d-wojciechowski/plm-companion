package pl.dwojciechowski.service

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import pl.dwojciechowski.proto.files.FileResponse

interface FileService {

    companion object {
        fun getInstance(project: Project): FileService {
            return ServiceManager.getService(project, FileService::class.java)
        }
    }

    fun getDirContent(path: String, fullExpand: Boolean = true): FileResponse

}
