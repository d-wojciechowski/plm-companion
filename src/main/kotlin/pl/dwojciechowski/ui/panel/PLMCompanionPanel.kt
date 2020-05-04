package pl.dwojciechowski.ui.panel

import com.intellij.openapi.project.Project
import javax.swing.JPanel

internal class PLMCompanionPanel(private val project: Project) {

    lateinit var content: JPanel
    private lateinit var customCommandPanel: JPanel
    private lateinit var fatButtonPanel: JPanel

    fun createUIComponents() {
        customCommandPanel = CommandSubPanel(project).content
        fatButtonPanel = FatButtonPanel(project).content
    }

}
