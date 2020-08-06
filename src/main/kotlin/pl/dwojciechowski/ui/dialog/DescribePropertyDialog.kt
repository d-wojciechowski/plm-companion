package pl.dwojciechowski.ui.dialog

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import pl.dwojciechowski.configuration.PluginConfiguration
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

    private val config = ServiceManager.getService(project, PluginConfiguration::class.java)
    private val commandService = ServiceManager.getService(project, RemoteService::class.java)
    private val ideControlService = ServiceManager.getService(project, IdeControlService::class.java)

    lateinit var content: JPanel
    private lateinit var inputPanel: JPanel

    private lateinit var describeButton: JButton
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

        describeButton.addActionListener { describePropertyButtonHandle() }

    }

    private fun describePropertyButtonHandle() {
        if (propertyNameTextField.text.isNullOrEmpty()) {
            Messages.showErrorDialog(
                project,
                getMessage("ui.dpd.error.empty.message"),
                getMessage("ui.dpd.error.empty.title")
            )
            return
        }
        val command = "xconfmanager -d ${propertyNameTextField.text}"
        val commandBean = CommandBean("", command, CommandBean.Type.PROPERTY_NAME)
        listModel.add(0, commandBean.clone())
        describedPropertiesList.selectedIndex = 0
        executeSelectedCommand()
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
            selectedIndex = locationToIndex(it?.point)
            if (it?.clickCount == 2) {
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
                commandService.executeStreaming(describedPropertiesList.selectedValue.clone())
            }
        }
        if (config.autoOpenCommandPane) {
            ideControlService.switchToCommandTab()
        }
    }

}