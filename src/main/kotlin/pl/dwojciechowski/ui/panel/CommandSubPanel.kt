package pl.dwojciechowski.ui.panel

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.ui.RawCommandLineEditor
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.model.CommandBean
import pl.dwojciechowski.service.WncConnectorService
import pl.dwojciechowski.ui.component.CommandList
import pl.dwojciechowski.ui.component.action.EditListAction
import java.awt.event.KeyEvent
import java.util.*
import javax.swing.DefaultListModel
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.ListSelectionModel

class CommandSubPanel(
    private val project: Project
) {

    private val config: PluginConfiguration = ServiceManager.getService(project, PluginConfiguration::class.java)
    private val windchillService = ServiceManager.getService(project, WncConnectorService::class.java)

    private val splitPattern = "|#*#$"

    lateinit var content: JPanel

    private lateinit var commandField: RawCommandLineEditor
    private lateinit var addButton: JButton
    private lateinit var executeCommandFromInputButton: JButton

    private lateinit var commandHistory: CommandList
    private lateinit var listModel: DefaultListModel<CommandBean>

    fun createUIComponents() {
        listModel = DefaultListModel()
        commandHistory = CommandList(listModel)
    }

    init {
        commandHistory.selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        config.commandsHistory.forEach {
            val split = it.split(splitPattern)
            listModel.add(0, CommandBean(split[0], split[1]))
        }

        commandHistory.addMousePressedListener {
            commandHistory.selectedIndex = commandHistory.locationToIndex(it?.point)
            if (it?.clickCount == 2) {
                executeSelectedCommand()
            }
        }

        commandHistory.setUpCommandHistoryRMBMenu()

        commandHistory.addKeyPressedListener {
            if (it?.keyChar?.toInt() == KeyEvent.VK_DELETE) {
                listModel.remove(commandHistory.selectedIndex)
            }
        }

        executeCommandFromInputButton.icon = AllIcons.RunConfigurations.TestState.Run
        executeCommandFromInputButton.addActionListener { executeFromInput() }

        addButton.icon = AllIcons.General.Add
        addButton.addActionListener { handleAddToListModel() }
    }

    private fun handleAddToListModel() {
        if (commandField.text.isEmpty()) {
            Messages.showMessageDialog(
                project, "Command field is empty", "No command provided", Messages.getErrorIcon()
            )
        } else if (Collections.list(listModel.elements()).stream().noneMatch { it.command == commandField.text }) {
            addToModel()
        } else {
            val confirmed = Messages.showConfirmationDialog(
                addButton, "Given command allready exists. Add anyway?", "", "Yes", "No"
            ) == 0
            if (confirmed) {
                addToModel()
            }
        }
    }

    private fun addToModel() {
        listModel.add(0, CommandBean("", commandField.text))
        dispose()
    }

    private fun CommandList.setUpCommandHistoryRMBMenu() {
        this.addRMBMenuEntry("Run") {
            executeSelectedCommand()
        }
            .addRMBMenuEntry("Edit", action = EditListAction(this))
            .addRMBMenuEntry("Delete") {
                listModel.remove(selectedIndex)
            }
            .addRMBMenuEntry("Alias", action = EditListAction(this, "name"))
    }

    private fun executeFromInput() {
        if (commandField.text.isEmpty()) {
            Messages.showMessageDialog(
                project, "Command field is empty", "No command provided", Messages.getErrorIcon()
            )
        } else {
            windchillService.executeStreaming(CommandBean("", commandField.text))
        }
    }

    private fun executeSelectedCommand() {
        if (commandHistory.selectedIndex == -1) {
            Messages.showMessageDialog(
                project, "No command selected", "Missing selection error", Messages.getErrorIcon()
            )
        } else {
            windchillService.executeStreaming(commandHistory.selectedValue.safeCopy())
        }
    }

    fun dispose() {
        config.commandsHistory = listModel.elements().toList()
            .reversed()
            .map { "${it.name}$splitPattern${it.command}" }
            .toMutableList()
    }

}