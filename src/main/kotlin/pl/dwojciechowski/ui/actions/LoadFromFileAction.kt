package pl.dwojciechowski.ui.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.vfs.VirtualFile
import pl.dwojciechowski.ui.dialog.LoadFromFileDialog

class LoadFromFileAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: throw Exception("Project not defined exception")
        val data = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(e.dataContext)
        data?.first()?.fileType
        val flatMap = data?.flatMap {
            if (it.isDirectory) {
                collectNestedFiles(it)
            } else {
                listOf(it)
            }
        }?.toList() ?: throw Exception("No files selected exception")

        LoadFromFileDialog(project, flatMap).showAndGet()
    }

    private fun collectNestedFiles(vfile: VirtualFile): List<VirtualFile> {
        return vfile.children.flatMap {
            if (it.isDirectory) {
                collectNestedFiles(it)
            } else {
                listOf(it)
            }
        }.toList()
    }
}
