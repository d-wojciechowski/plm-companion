package pl.dwojciechowski.execution.command

import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import pl.dwojciechowski.configuration.ProjectPluginConfiguration
import pl.dwojciechowski.model.CommandBean
import pl.dwojciechowski.ui.component.CommandList
import javax.swing.*

class RemoteCommandSettingsEditor(project: Project) : SettingsEditor<RemoteCommandRunConfig>() {

    private val config = project.getService(ProjectPluginConfiguration::class.java)

    private lateinit var myPanel: JPanel

    private lateinit var commandTF: JTextField
    private lateinit var commandHistory: CommandList
    private lateinit var listModel: DefaultListModel<CommandBean>
    private lateinit var async: JCheckBox

    private val splitPattern = "|#*#$"

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
                commandTF.text = commandHistory.selectedValue.command
            }
        }
    }

    override fun resetEditorFrom(s: RemoteCommandRunConfig) {
        commandTF.text = s.settings.command
        async.isSelected = s.settings.async
    }

    override fun applyEditorTo(s: RemoteCommandRunConfig) {
        s.settings.command = commandTF.text
        s.settings.async = async.isSelected
    }

    override fun createEditor() = myPanel

}