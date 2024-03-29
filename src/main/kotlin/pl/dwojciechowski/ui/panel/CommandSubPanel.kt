package pl.dwojciechowski.ui.panel

import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.showYesNoDialog
import com.intellij.ui.RawCommandLineEditor
import com.intellij.util.ui.UIUtil
import pl.dwojciechowski.configuration.ProjectPluginConfiguration
import pl.dwojciechowski.i18n.PluginBundle.getMessage
import pl.dwojciechowski.model.CommandBean
import pl.dwojciechowski.service.IdeControlService
import pl.dwojciechowski.service.RemoteService
import pl.dwojciechowski.ui.component.CommandList
import pl.dwojciechowski.ui.component.EtchedTitleBorder
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

    private val config = project.getService(ProjectPluginConfiguration::class.java)
    private val windchillService = project.getService(RemoteService::class.java)
    private val ideControlService = project.getService(IdeControlService::class.java)

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

        content.border = EtchedTitleBorder(getMessage("ui.cp.panel.title"))
    }

    private fun handleAddToListModel() {
        if (commandField.text.isEmpty()) {
            Messages.showMessageDialog(
                project,
                getMessage("ui.cp.error.empty_command.message"),
                getMessage("ui.cp.error.empty_command.title"),
                Messages.getErrorIcon()
            )
        } else if (Collections.list(listModel.elements()).stream().noneMatch { it.command == commandField.text }) {
            addToModel()
        } else {
            val confirmed = showYesNoDialog(
                title = "",
                message = getMessage("ui.cp.error.existing_command.message"),
                project = project,
                icon = UIUtil.getQuestionIcon()
            )
            if (confirmed) addToModel()
        }
    }

    private fun addToModel() {
        listModel.add(0, CommandBean("", commandField.text))
        saveToConfig()
    }

    private fun CommandList.setUpCommandHistoryRMBMenu() {
        addRMBMenuEntry(getMessage("ui.cp.rmb.run")) {
            executeSelectedCommand()
        }
        addRMBMenuEntry(getMessage("ui.cp.rmb.delete")) {
            listModel.remove(selectedIndex)
            saveToConfig()
        }
        addRMBMenuEntry(getMessage("ui.cp.rmb.edit"), action = EditListAction(this) { saveToConfig() })
        addRMBMenuEntry(getMessage("ui.cp.rmb.alias"), action = EditListAction(this, "name") { saveToConfig() })
    }

    private fun executeFromInput() {
        if (commandField.text.isEmpty()) {
            Messages.showMessageDialog(
                project, "Command field is empty", "No command provided", Messages.getErrorIcon()
            )
        } else {
            ApplicationManager.getApplication().invokeLater {
                windchillService.executeStreaming(CommandBean("", commandField.text))
            }
        }
        if (config.autoOpenCommandPane) {
            ideControlService.switchToCommandTab()
        }
    }

    private fun executeSelectedCommand() {
        if (commandHistory.selectedIndex == -1) {
            Messages.showMessageDialog(
                project,
                getMessage("ui.cp.error.empty_command.message"),
                getMessage("ui.cp.error.empty_command.title"),
                Messages.getErrorIcon()
            )
        } else {
            ApplicationManager.getApplication().invokeLater {
                windchillService.executeStreaming(commandHistory.selectedValue.clone())
            }
        }
        if (config.autoOpenCommandPane) {
            ideControlService.switchToCommandTab()
        }
    }

    private fun saveToConfig() {
        config.commandsHistory = listModel.elements().toList()
            .reversed()
            .map { "${it.name}$splitPattern${it.command}" }
            .toMutableList()
    }

}
