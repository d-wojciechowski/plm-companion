package pl.dwojciechowski.ui.panel

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import pl.dwojciechowski.service.WncConnectorService
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTextArea

class CommandControlPanel(project: Project) : SimpleToolWindowPanel(false, true) {

    private val commandService: WncConnectorService =
        ServiceManager.getService(project, WncConnectorService::class.java)

    lateinit var panel: JPanel

    private lateinit var contentArea: JTextArea
    private lateinit var clearButton: JButton
    private lateinit var autoScrollJButton: JButton

    private var autoScroll = true

    init {
        this.add(panel)

        clearButton.addActionListener { contentArea.text = "" }
        clearButton.icon = AllIcons.Actions.GC

        autoScrollJButton.icon = AllIcons.General.AutoscrollFromSource
        autoScrollJButton.addActionListener {
            autoScroll = !autoScroll
            autoScrollJButton.icon = if (autoScroll) AllIcons.General.AutoscrollFromSource else AllIcons.General.ZoomOut
        }

        commandService.getOutputSubject().subscribe {
            contentArea.append(it + "\n")
            if (autoScroll) contentArea.caretPosition = contentArea.document.length
        }

    }

}
