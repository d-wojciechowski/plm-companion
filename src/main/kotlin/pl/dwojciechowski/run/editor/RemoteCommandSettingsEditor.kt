package pl.dwojciechowski.run.editor

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.model.CommandBean
import pl.dwojciechowski.run.config.RemoteCommandConfigurationBase
import pl.dwojciechowski.ui.component.CommandList
import javax.swing.*

class RemoteCommandSettingsEditor(private val project: Project) : SettingsEditor<RemoteCommandConfigurationBase>() {

    private val config: PluginConfiguration = ServiceManager.getService(project, PluginConfiguration::class.java)

    private lateinit var myPanel: JPanel

    private lateinit var commandTF: JTextField
    private lateinit var commandHistory: CommandList
    private lateinit var listModel: DefaultListModel<CommandBean>

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

    override fun resetEditorFrom(s: RemoteCommandConfigurationBase) {
        commandTF.text = s.settings.command
    }

    override fun applyEditorTo(s: RemoteCommandConfigurationBase) {
        s.settings.command = commandTF.text
    }

    override fun createEditor(): JComponent {
        return myPanel
    }


}