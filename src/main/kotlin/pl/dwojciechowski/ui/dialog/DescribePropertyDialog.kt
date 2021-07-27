package pl.dwojciechowski.ui.dialog

import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.showYesNoDialog
import pl.dwojciechowski.configuration.ProjectPluginConfiguration
import pl.dwojciechowski.i18n.PluginBundle.getMessage
import pl.dwojciechowski.model.CommandBean
import pl.dwojciechowski.service.IdeControlService
import pl.dwojciechowski.service.RemoteService
import pl.dwojciechowski.ui.component.CommandList
import pl.dwojciechowski.ui.component.EtchedTitleBorder
import pl.dwojciechowski.ui.component.action.EditListAction
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import javax.swing.*

class DescribePropertyDialog(private val project: Project) : DialogWrapper(project), Disposable {

    private val config = project.getService(ProjectPluginConfiguration::class.java)
    private val commandService = project.getService(RemoteService::class.java)
    private val ideControlService = project.getService(IdeControlService::class.java)

    lateinit var content: JPanel
    private lateinit var inputPanel: JPanel

    private lateinit var addButton: JButton
    private lateinit var executeCommandFromInputButton: JButton
    private lateinit var propertyNameTextField: JTextField
    private lateinit var describedPropertiesList: CommandList

    private lateinit var listModel: DefaultListModel<CommandBean>

    fun createUIComponents() {
        listModel = DefaultListModel()
        describedPropertiesList = CommandList(listModel)
    }

    init {
        init()
        title = getMessage("ui.dpd.title")

        inputPanel.border = EtchedTitleBorder(getMessage("ui.dpd.props.input.panel"))
        describedPropertiesList.init()
        config.propertiesHistory.forEach {
            listModel.add(0, CommandBean("", it, CommandBean.Type.PROPERTY_NAME))
        }

        addButton.icon = AllIcons.General.Add
        addButton.addActionListener {
            if (isInputNotEmpty() && isInputCommandUnique()) {
                saveInputCommand()
            }
        }

        executeCommandFromInputButton.icon = AllIcons.RunConfigurations.TestState.Run
        executeCommandFromInputButton.addActionListener {
            if (isInputNotEmpty()) {
                executeSelectedCommand()
            }
        }

    }

    private fun isInputCommandUnique(): Boolean {
        val inputCommand = getInputCommand()
        if (listModel.elements().toList().find { it.command == inputCommand } != null) {
            return showYesNoDialog(
                getMessage("ui.dpd.error.duplicate.title"),
                getMessage("ui.dpd.error.duplicate.message"),
                project,
                icon = Messages.getQuestionIcon()
            )
        }
        return true
    }

    private fun getInputCommand() = "xconfmanager -d ${propertyNameTextField.text}"

    private fun isInputNotEmpty(): Boolean {
        if (propertyNameTextField.text.isNullOrEmpty()) {
            Messages.showErrorDialog(
                project,
                getMessage("ui.dpd.error.empty.message"),
                getMessage("ui.dpd.error.empty.title")
            )
            return false
        }
        return true
    }

    private fun saveInputCommand() {
        val commandBean = CommandBean("", getInputCommand(), CommandBean.Type.PROPERTY_NAME)
        listModel.add(0, commandBean.clone())
        describedPropertiesList.selectedIndex = 0
        saveToConfig()
    }

    override fun createCenterPanel() = content
    override fun dispose() = super.dispose()

    override fun getOKAction(): Action =
        object : AbstractAction("OK") {
            override fun actionPerformed(e: ActionEvent?) {
                executeSelectedCommand()
                if (describedPropertiesList.selectedIndex == -1) {
                    return
                }
                dispose()
                close(OK_EXIT_CODE)
            }
        }

    private fun saveToConfig() {
        config.propertiesHistory = listModel.elements().toList()
            .reversed()
            .map { it.command }
            .toMutableList()
    }

    private fun CommandList.init() {
        addRMBMenuEntry(getMessage("ui.cp.rmb.run")) { executeSelectedCommand() }
        addRMBMenuEntry(getMessage("ui.cp.rmb.edit"), action = EditListAction(this) { saveToConfig() })
        addRMBMenuEntry(getMessage("ui.clp.rmb.delete")) {
            listModel.remove(selectedIndex)
            saveToConfig()
        }

        addKeyPressedListener {
            if (it?.keyChar?.toInt() == KeyEvent.VK_DELETE) {
                listModel.remove(selectedIndex)
                saveToConfig()
            }
        }
        addMousePressedListener {
            if (it?.clickCount == 2) {
                selectedIndex = locationToIndex(it.point)
                executeSelectedCommand()
            }
        }
    }

    private fun executeSelectedCommand() {
        if (describedPropertiesList.selectedIndex == -1) {
            Messages.showErrorDialog(
                project,
                getMessage("ui.dpd.error.nonselected.message"),
                getMessage("ui.dpd.error.nonselected.title")
            )
        } else {
            ApplicationManager.getApplication().invokeLater {
                ideControlService.withAutoOpen {
                    commandService.executeStreaming(describedPropertiesList.selectedValue.clone())
                }
            }
        }
    }

}
