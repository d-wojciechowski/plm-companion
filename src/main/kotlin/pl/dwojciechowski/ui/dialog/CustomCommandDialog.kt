package pl.dwojciechowski.ui.dialog

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBList
import org.picocontainer.Disposable
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.proto.Service
import javax.swing.DefaultListModel
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTextField

class CustomCommandDialog(
    private val project: Project
) : DialogWrapper(project), Disposable {

    private val config: PluginConfiguration = ServiceManager.getService(project, PluginConfiguration::class.java)

    lateinit var content: JPanel

    private lateinit var commandField: JTextField
    private lateinit var argsField: JTextField
    private lateinit var addButton: JButton

    private lateinit var commandHistory: JBList<Service.Command>

    fun createUIComponents() {
        commandHistory = JBList(Service.Command.newBuilder()
            .setCommand("windchill")
            .setArgs("start")
            .build())
    }

    init {
        init()
        addButton.icon = AllIcons.General.Add
        addButton.addActionListener {
            (commandHistory.model as DefaultListModel<Service.Command>).addElement(Service.Command.newBuilder()
                .setCommand(commandField.text)
                .setArgs(argsField.text)
                .build())
        }

    }

    override fun dispose() {
        super.dispose()
    }

    override fun createCenterPanel() = content

}
