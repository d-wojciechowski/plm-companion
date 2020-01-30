package pl.dwojciechowski.ui.panel

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.components.JBTextArea
import pl.dwojciechowski.service.WncConnectorService
import javax.swing.JButton
import javax.swing.JPanel

class CommandControlPanel(
    private val project: Project
) : SimpleToolWindowPanel(false, true) {

    private val commandService: WncConnectorService =
        ServiceManager.getService(project, WncConnectorService::class.java)

    lateinit var panel: JPanel

    private lateinit var contentArea: JBTextArea
    private lateinit var clearButton: JButton

    init {
        this.add(panel)

        clearButton.addActionListener { contentArea.text = "" }
        clearButton.icon = AllIcons.Actions.GC

        commandService.getOutputSubject().subscribe {
            contentArea.append(it + "\n")
            contentArea.caretPosition = contentArea.document.length
        }

    }

}
