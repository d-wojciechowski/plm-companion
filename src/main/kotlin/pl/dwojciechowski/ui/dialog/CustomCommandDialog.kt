package pl.dwojciechowski.ui.dialog

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBList
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.service.ActionExecutor
import pl.dwojciechowski.service.WncConnectorService
import pl.dwojciechowski.ui.component.CommandRepresenation
import javax.swing.*

class CustomCommandDialog(
    private val project: Project
) {

    private val config: PluginConfiguration = ServiceManager.getService(project, PluginConfiguration::class.java)
    private val actionExecutor = ServiceManager.getService(project, ActionExecutor::class.java)
    private val windchillService = ServiceManager.getService(project, WncConnectorService::class.java)

    private val splitPattern = "|#*#$"

    lateinit var content: JPanel

    private lateinit var commandField: JTextField
    private lateinit var addButton: JButton
    private lateinit var executeSelectedAction: JButton
    private lateinit var executeCommandFromInputButton: JButton
    private lateinit var removeSelectionButton: JButton

    private lateinit var commandHistory: JBList<CommandRepresenation>
    private lateinit var listModel: DefaultListModel<CommandRepresenation>

    fun createUIComponents() {
        listModel = DefaultListModel()
        commandHistory = JBList<CommandRepresenation>(listModel)
    }

    init {
        commandHistory.selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        config.commandsHistory.forEach {
            val split = it.split(splitPattern)
            listModel.add(0, CommandRepresenation(split[0], split[1]))
            dispose()
        }

        executeCommandFromInputButton.icon = AllIcons.RunConfigurations.TestState.Run
        executeCommandFromInputButton.addActionListener { executeFromInput() }

        executeSelectedAction.icon = AllIcons.RunConfigurations.TestState.Run
        executeSelectedAction.addActionListener { executeSelectedCommand() }

        removeSelectionButton.icon = AllIcons.General.Remove
        removeSelectionButton.addActionListener { listModel.remove(commandHistory.selectedIndex) }

        addButton.icon = AllIcons.General.Add
        addButton.addActionListener {
            listModel.add(0, CommandRepresenation(commandField.text, commandField.text))
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
                windchillService.execCommand(CommandRepresenation("", commandField.text).getCommand())
            }
            true
        }
    }

    private fun executeSelectedCommand(): Boolean {
        return if (commandHistory.selectedIndex == -1) {
            Messages.showMessageDialog(
                project, "No command selected", "Missing selection error", Messages.getErrorIcon()
            )
            false
        } else {
            val command = commandHistory.selectedValue
            actionExecutor.executeAction(command.name) {
                val execCommand = windchillService.execCommand(command.getCommand())
                execCommand
            }
            true
        }
    }

    fun dispose() {
        config.commandsHistory = listModel.elements().toList()
            .reversed()
            .map { "${it.name}$splitPattern${it.command}" }
            .toMutableList()
    }


//    private fun DefaultListModel<*>.getSelectedCommand(): Command {
//        return buildCommand(this.get(commandHistory.selectedIndex) as String)
//    }
//
//    private fun Command.getActionName(): String {
//        val actionName = listModel.getValueAt(commandHistory.selectedRow, 0) as String
//        return if (actionName.isNotEmpty()) command else "$command $args"
//    }
//

}
