package pl.dwojciechowski.ui.panel

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import pl.dwojciechowski.service.WncConnectorService
import pl.dwojciechowski.ui.component.CommandList
import pl.dwojciechowski.ui.component.CommandRepresenation
import java.awt.event.KeyEvent
import javax.swing.DefaultListModel
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

    private lateinit var list: CommandList
    private lateinit var listModel: DefaultListModel<CommandRepresenation>

    fun createUIComponents() {
        listModel = DefaultListModel()
        list = CommandList(listModel)
    }

    init {
        this.add(panel)

        clearButton.addActionListener { contentArea.text = "" }
        clearButton.icon = AllIcons.Actions.GC

        list.addRMBMenuEntry("ADD") {
            listModel.addElement(CommandRepresenation("TEST", "LEL"))
        }
        list.addRMBMenuEntry("Delete") {
            listModel.remove(list.selectedIndex)
        }
        list.addKeyPressedListener {
            if (it?.keyChar?.toInt() == KeyEvent.VK_DELETE) {
                listModel.remove(list.selectedIndex)
            }
        }

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
