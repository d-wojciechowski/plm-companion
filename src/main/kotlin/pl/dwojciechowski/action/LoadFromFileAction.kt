package pl.dwojciechowski.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.vfs.VirtualFile
import pl.dwojciechowski.ui.dialog.LoadFromFileDialog

class LoadFromFileAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: throw Exception("Project not defined exception")
        val flatMap = PlatformDataKeys.VIRTUAL_FILE_ARRAY
            .getData(e.dataContext)
            ?.flatMap(this::collectNested)
            ?.toList() ?: throw Exception("No files selected exception")
        LoadFromFileDialog(project, flatMap).showAndGet()
    }

    private fun collectNested(it: VirtualFile): List<VirtualFile> {
        return if (it.isDirectory) {
            it.children.flatMap(this::collectNested).toList()
        } else {
            listOf(it)
        }
    }

}
