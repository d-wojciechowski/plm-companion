package pl.dwojciechowski.run.editor

import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import pl.dwojciechowski.run.config.RemoteCommandConfigurationBase
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class RemoteCommandSettingsEditor(private val project: Project) : SettingsEditor<RemoteCommandConfigurationBase>() {

    private lateinit var myPanel: JPanel

    private lateinit var commandTF: JTextField

//    private lateinit var commandSubPanel: CommandSubPanel
//    private lateinit var commandSubPanelPanel :JPanel

    private fun createUIComponents() {
//        commandSubPanel = CommandSubPanel(project)
//        commandSubPanelPanel = commandSubPanel.content
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