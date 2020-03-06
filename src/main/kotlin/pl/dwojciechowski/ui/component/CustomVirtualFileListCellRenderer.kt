package pl.dwojciechowski.ui.component

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vcs.changes.ui.VirtualFileListCellRenderer
import com.intellij.ui.SimpleTextAttributes

class CustomVirtualFileListCellRenderer(
    private val project: Project
) : VirtualFileListCellRenderer(project) {

    override fun putParentPath(value: Any, path: FilePath, self: FilePath) {
        val parentFile = path.ioFile.parentFile
        if (parentFile != null) {
            val parentPath = project.basePath?.length?.let { parentFile.absolutePath.substring(it) }
            append(" (", SimpleTextAttributes.GRAYED_ATTRIBUTES)
            putParentPathImpl(value, parentPath, self)
            append(")", SimpleTextAttributes.GRAYED_ATTRIBUTES)
        }
    }

}