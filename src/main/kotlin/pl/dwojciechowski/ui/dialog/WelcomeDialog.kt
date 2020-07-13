package pl.dwojciechowski.ui.dialog

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.Action
import javax.swing.JPanel

class WelcomeDialog(
    private val project: Project
) : DialogWrapper(project) {

    private lateinit var rootPane: JPanel

    init {
        init()
        title = "Remote File Picker Dialog"

    }

    override fun getOKAction(): Action =
        object : AbstractAction("OK") {
            override fun actionPerformed(e: ActionEvent?) {
            }
        }


    override fun createCenterPanel() = rootPane

}