package pl.dwojciechowski.ui.component

import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import pl.dwojciechowski.ui.dialog.RemoteFilePickerDialog
import javax.swing.JButton

class RemotePickerButton(
    private val project: Project,
    private val startPath: String = "",
    private val method: (List<String>) -> Unit
) : JButton(AllIcons.General.OpenDisk) {

    init {
        addActionListener {
            val remoteFilePickerDialog = RemoteFilePickerDialog(project, startPath)
            if (remoteFilePickerDialog.showAndGet()) {
                method(remoteFilePickerDialog.chosenItems)
            }
        }
    }

}