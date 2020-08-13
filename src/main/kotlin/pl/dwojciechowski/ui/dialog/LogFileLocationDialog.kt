package pl.dwojciechowski.ui.dialog

import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import pl.dwojciechowski.i18n.PluginBundle.getMessage
import java.awt.event.ActionEvent
import javax.swing.*

class LogFileLocationDialog(
    private val project: Project,
    initTFValue: String = "",
    private val fileOnlySelection: Boolean = false
) : DialogWrapper(project), Disposable {

    lateinit var content: JPanel

    private lateinit var logFileLocationTF: JTextField
    private lateinit var remotePickerButton: JButton

    var result = initTFValue

    init {
        init()
        title = getMessage("ui.dialog.lfl.title")

        remotePickerButton.icon = AllIcons.General.OpenDisk
        logFileLocationTF.text = initTFValue
        remotePickerButton.addActionListener {
            val remoteFilePickerDialog =
                RemoteFilePickerDialog(project, logFileLocationTF.text, true, fileOnlySelection, false)
            if (remoteFilePickerDialog.showAndGet()) {
                logFileLocationTF.text = remoteFilePickerDialog.chosenItems.first()
            }
        }
    }

    override fun createCenterPanel() = content
    override fun dispose() = super.dispose()

    override fun getOKAction(): Action =
        object : AbstractAction("OK") {
            override fun actionPerformed(e: ActionEvent?) {
                if (logFileLocationTF.text.isEmpty()) {
                    Messages.showErrorDialog(
                        project,
                        getMessage("ui.dialog.lfl.emptyfile.message"),
                        getMessage("ui.dialog.lfl.emptyfile.title")
                    )
                    return
                }
                dispose()
                close(OK_EXIT_CODE)
                result = logFileLocationTF.text
            }
        }

    override fun getCancelAction(): Action =
        object : AbstractAction("Cancel") {
            override fun actionPerformed(e: ActionEvent?) {
                dispose()
                close(CANCEL_EXIT_CODE)
            }
        }

}