package pl.dwojciechowski.ui.dialog

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBList
import pl.dwojciechowski.configuration.ProjectPluginConfiguration
import pl.dwojciechowski.i18n.PluginBundle
import pl.dwojciechowski.ui.PLMPluginNotification
import java.awt.event.ActionEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*

class FileCopyConfigurationDialog(private val project: Project) : DialogWrapper(project), Disposable {

    private val config = ServiceManager.getService(project, ProjectPluginConfiguration::class.java)

    lateinit var content: JPanel

    private lateinit var configList: JBList<String>
    private lateinit var folderName: JTextField
    private lateinit var ignoredList: JTextField
    private lateinit var addButton: JButton


    init {
        title = PluginBundle.getMessage("ui.config.filecopy.foldercopyconfig")

        init()
    }

    private fun JTextField.addOnKeyEvent(method: () -> Unit) {
        this.addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent?) {
                super.keyReleased(e)
                method()
            }
        })
    }

    override fun createCenterPanel() = content
    override fun dispose() = super.dispose()

    private fun initFromConfig() {
    }

    private fun saveConfig() {
    }

    override fun getOKAction(): Action =
        object : AbstractAction("OK") {
            override fun actionPerformed(e: ActionEvent?) {
                saveConfig()
                PLMPluginNotification.settingsSaved(project)
                dispose()
            }
        }

    override fun getCancelAction(): Action =
        object : AbstractAction("Cancel") {
            override fun actionPerformed(e: ActionEvent?) {
                dispose()
            }
        }

}