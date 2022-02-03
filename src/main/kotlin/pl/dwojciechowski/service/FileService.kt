package pl.dwojciechowski.service

import com.intellij.openapi.project.Project
import pl.dwojciechowski.proto.files.FileResponse

interface FileService {

    companion object {
        fun getInstance(project: Project): FileService {
            return project.getService(FileService::class.java)
        }
    }

    fun getDirContent(path: String, fullExpand: Boolean = true): FileResponse

}
