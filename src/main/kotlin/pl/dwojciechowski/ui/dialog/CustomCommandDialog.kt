package pl.dwojciechowski.ui.dialog

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.components.JBList
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.service.ActionExecutor
import pl.dwojciechowski.service.WncConnectorService
import pl.dwojciechowski.ui.component.CommandListRMBMenu
import pl.dwojciechowski.ui.component.CommandRepresenation
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class CustomCommandDialog(
    private val project: Project
) {

    private val config: PluginConfiguration = ServiceManager.getService(project, PluginConfiguration::class.java)
    private val actionExecutor = ServiceManager.getService(project, ActionExecutor::class.java)
    private val windchillService = ServiceManager.getService(project, WncConnectorService::class.java)

    private val splitPattern = "|#*#$"

    lateinit var content: JPanel

    private lateinit var commandField: RawCommandLineEditor
    private lateinit var addButton: JButton
    private lateinit var executeCommandFromInputButton: JButton

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
        }

        commandHistory.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent?) {
                commandHistory.selectedIndex = commandHistory.locationToIndex(e?.point)
                if (SwingUtilities.isRightMouseButton(e)) {
                    CommandListRMBMenu(commandHistory)
                        .addItem("Run",0){
                            executeSelectedCommand()
                        }.show(e)
                } else if(e?.clickCount == 2){
                    executeSelectedCommand()
                }
            }
        })

        executeCommandFromInputButton.icon = AllIcons.RunConfigurations.TestState.Run
        executeCommandFromInputButton.addActionListener { executeFromInput() }

        addButton.icon = AllIcons.General.Add
        addButton.addActionListener {
            if (commandField.text.isEmpty()) {
                Messages.showMessageDialog(
                    project, "Command field is empty", "No command provided", Messages.getErrorIcon()
                )
            } else {
                listModel.add(0, CommandRepresenation("", commandField.text))
                dispose()
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

}
