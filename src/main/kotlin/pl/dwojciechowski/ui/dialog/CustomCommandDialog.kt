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
import pl.dwojciechowski.proto.Service
import pl.dwojciechowski.service.ActionExecutor
import pl.dwojciechowski.service.WncConnectorService
import java.awt.event.ActionEvent
import java.util.*
import javax.swing.*
import javax.swing.table.DefaultTableModel

class CustomCommandDialog(
    private val project: Project
) : DialogWrapper(project), Disposable {

    private val config: PluginConfiguration = ServiceManager.getService(project, PluginConfiguration::class.java)
    private val actionExecutor = ServiceManager.getService(project, ActionExecutor::class.java)
    private val windchillService = ServiceManager.getService(project, WncConnectorService::class.java)

    private val splitPattern = "|#*#$"

    lateinit var content: JPanel

    private lateinit var commandField: JTextField
    private lateinit var argsField: JTextField
    private lateinit var addButton: JButton
    private lateinit var executeSelectedAction: JButton
    private lateinit var executeCommandFromInputButton: JButton
    private lateinit var removeSelectionButton: JButton

    private lateinit var commandHistory: JBTable
    private lateinit var tableModel: DefaultTableModel

    fun createUIComponents() {
        tableModel = object : DefaultTableModel(arrayOf("Command", "Args"), 0) {
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
            tableModel.addRow(arrayOf(commandField.text, argsField.text))
        }
    }

    override fun getOKAction(): Action =
        object : AbstractAction("OK") {
            override fun actionPerformed(e: ActionEvent?) {
                if (executeSelectedCommand()) {
                    dispose()
                    close(OK_EXIT_CODE)
                }
            }
        }

    private fun executeFromInput(): Boolean {
        return if (commandField.text.isEmpty()) {
            Messages.showMessageDialog(
                project, "Command field is empty", "No command provided", Messages.getErrorIcon()
            )
            false
        } else {
            val command = buildCommand(commandField.text, argsField.text)
            val actionName = "${command.command} ${command.args}"
            actionExecutor.executeAction(actionName) {
                windchillService.execCommand(command)
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
            val command = buildCommand(
                tableModel.getValueAt(commandHistory.selectedRow, 0) as String,
                tableModel.getValueAt(commandHistory.selectedRow, 1) as String
            )
            val actionName = "${command.command} ${command.args}"
            actionExecutor.executeAction(actionName) {
                windchillService.execCommand(command)
            }
            true
        }
    }

    override fun getCancelAction(): Action =
        object : AbstractAction("Cancel") {
            override fun actionPerformed(e: ActionEvent?) {
                dispose()
                close(CANCEL_EXIT_CODE)
            }
        }

    override fun dispose() {
        config.commandsHistory = tableModel.dataVector
            .reversed()
            .map { (it as Vector<*>).joinToString(splitPattern) }
            .toMutableList()
        super.dispose()
    }

    override fun createCenterPanel() = content

    private fun buildCommand(command: String, args: String) =
        Service.Command.newBuilder()
            .setCommand(command)
            .setArgs(args)
            .build()


    private fun String.toRow(): Array<Any>? = split(splitPattern).toArray(arrayOf())

}
