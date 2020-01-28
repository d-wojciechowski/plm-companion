package pl.dwojciechowski.ui.dialog

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.ui.table.JBTable
import com.intellij.util.containers.toArray
import org.picocontainer.Disposable
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.proto.commands.Command
import pl.dwojciechowski.service.ActionExecutor
import pl.dwojciechowski.service.WncConnectorService
import java.util.*
import javax.swing.*
import javax.swing.table.DefaultTableModel

class CustomCommandDialog(
    private val project: Project,
    private val customActionButton: JButton
) : DialogWrapper(project, false, IdeModalityType.MODELESS), Disposable {

    private val config: PluginConfiguration = ServiceManager.getService(project, PluginConfiguration::class.java)
    private val actionExecutor = ServiceManager.getService(project, ActionExecutor::class.java)
    private val windchillService = ServiceManager.getService(project, WncConnectorService::class.java)

    private val splitPattern = "|#*#$"

    lateinit var content: JPanel

    private lateinit var nameField: JTextField
    private lateinit var commandField: JTextField
    private lateinit var addButton: JButton
    private lateinit var executeSelectedAction: JButton
    private lateinit var executeCommandFromInputButton: JButton
    private lateinit var removeSelectionButton: JButton

    private lateinit var commandHistory: JBTable
    private lateinit var tableModel: DefaultTableModel

    fun createUIComponents() {
        tableModel = object : DefaultTableModel(arrayOf("Name", "Command"), 0) {
            override fun addRow(rowData: Array<Any>) {
                getDataVector().reverse()
                super.addRow(rowData)
                getDataVector().reverse()
            }
        }
        commandHistory = JBTable(tableModel)
    }

    init {
        init()
        title = "Custom Command Execution"
        customActionButton.isEnabled = false
        commandHistory.selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        config.commandsHistory.forEach { tableModel.addRow(it.toRow()) }

        executeCommandFromInputButton.icon = AllIcons.RunConfigurations.TestState.Run
        executeCommandFromInputButton.addActionListener { executeFromInput() }

        executeSelectedAction.icon = AllIcons.RunConfigurations.TestState.Run
        executeSelectedAction.addActionListener { executeSelectedCommand() }

        removeSelectionButton.icon = AllIcons.General.Remove
        removeSelectionButton.addActionListener { tableModel.removeRow(commandHistory.selectedRow) }

        addButton.icon = AllIcons.General.Add
        addButton.addActionListener {
            tableModel.addRow(arrayOf(nameField.text, commandField.text))
        }
    }

    private fun executeFromInput(): Boolean {
        return if (commandField.text.isEmpty()) {
            Messages.showMessageDialog(
                project, "Command field is empty", "No command provided", Messages.getErrorIcon()
            )
            false
        } else {
            actionExecutor.executeAction(commandField.text) {
                windchillService.execCommand(buildCommand(commandField.text))
            }
            true
        }
    }

    private fun executeSelectedCommand(): Boolean {
        return if (commandHistory.selectedRow == -1) {
            Messages.showMessageDialog(
                project, "No command selected", "Missing selection error", Messages.getErrorIcon()
            )
            false
        } else {
            val command = tableModel.getSelectedCommand()
            actionExecutor.executeAction(command.getActionName()) {
                val execCommand = windchillService.execCommand(command)
                execCommand
            }
            true
        }
    }

    override fun dispose() {
        config.commandsHistory = tableModel.dataVector
            .reversed()
            .map { (it as Vector<*>).joinToString(splitPattern) }
            .toMutableList()
        customActionButton.isEnabled = true
        super.dispose()
    }


    private fun String.toRow(): Array<Any>? = split(splitPattern).toArray(arrayOf())

    override fun createCenterPanel() = content
    override fun createActions(): Array<Action> = arrayOf()

    private fun DefaultTableModel.getSelectedCommand(): Command {
        return buildCommand(this.getValueAt(commandHistory.selectedRow, 1) as String)
    }

    private fun Command.getActionName(): String {
        val actionName = tableModel.getValueAt(commandHistory.selectedRow, 0) as String
        return if (actionName.isNotEmpty()) command else "$command $args"
    }

    private fun buildCommand(command: String): Command {
        val split = command.split(' ', limit = 1)
        return Command.newBuilder()
            .setCommand(split[0])
            .setArgs(if (split.size > 1) split[1] else "")
            .build()
    }

}
