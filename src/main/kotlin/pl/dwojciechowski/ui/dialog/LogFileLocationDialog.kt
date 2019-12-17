package pl.dwojciechowski.ui.dialog

import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import io.reactivex.rxjava3.subjects.PublishSubject
import java.awt.event.ActionEvent
import javax.swing.*

class LogFileLocationDialog(
    private val project: Project,
    private val logLocation: PublishSubject<String>,
    initTFValue: String = ""
) : DialogWrapper(project), org.picocontainer.Disposable {

    lateinit var content: JPanel

    private lateinit var logFileLocationTF: JTextField
    private lateinit var remotePickerButton: JButton

    init {
        init()
        title = "Log File Location"

        remotePickerButton.icon = AllIcons.General.OpenDisk
        logFileLocationTF.text = initTFValue
        remotePickerButton.addActionListener {
            val remoteFilePickerDialog = RemoteFilePickerDialog(project, logFileLocationTF.text, true, false)
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
                        "Please type file name or select cancel",
                        "Value must not be empty!"
                    )
                    return
                }
                this@LogFileLocationDialog.logLocation.onNext(logFileLocationTF.text)
                dispose()
                close(OK_EXIT_CODE)
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