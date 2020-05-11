package pl.dwojciechowski.run.editor

import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComponentWithBrowseButton
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import pl.dwojciechowski.run.config.RemoteCommandConfigurationBase
import pl.dwojciechowski.ui.panel.CommandSubPanel
import javax.swing.JComponent
import javax.swing.JPanel

class RemoteCommandSettingsEditor(private val project: Project) : SettingsEditor<RemoteCommandConfigurationBase>() {

    private lateinit var myPanel: JPanel
    private var myMainClass: LabeledComponent<ComponentWithBrowseButton<*>>? = null

    private lateinit var commandSubPanel: CommandSubPanel
    private lateinit var commandSubPanelPanel :JPanel

    private fun createUIComponents() {
        commandSubPanel = CommandSubPanel(project)
        commandSubPanelPanel = commandSubPanel.content

        myMainClass = LabeledComponent()
        myMainClass!!.component = TextFieldWithBrowseButton()


    }

    override fun resetEditorFrom(s: RemoteCommandConfigurationBase) {}

    override fun applyEditorTo(s: RemoteCommandConfigurationBase) {
    }

    override fun createEditor(): JComponent {
        return myPanel
    }


}