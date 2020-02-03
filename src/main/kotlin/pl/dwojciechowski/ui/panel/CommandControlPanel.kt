package pl.dwojciechowski.ui.panel

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import io.reactivex.rxjava3.disposables.Disposable
import pl.dwojciechowski.model.CommandBean
import pl.dwojciechowski.service.WncConnectorService
import pl.dwojciechowski.ui.component.CommandList
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
    private lateinit var lastSubscribe: Disposable


    private lateinit var list: CommandList
    private lateinit var listModel: DefaultListModel<CommandBean>

    fun createUIComponents() {
        listModel = DefaultListModel()
        list = CommandList(listModel)
    }

    init {
        this.add(panel)

        clearButton.addActionListener { contentArea.text = "" }
        clearButton.icon = AllIcons.Actions.GC

        autoScrollJButton.icon = AllIcons.General.AutoscrollFromSource
        autoScrollJButton.addActionListener {
            autoScroll = !autoScroll
            autoScrollJButton.icon = if (autoScroll) AllIcons.General.AutoscrollFromSource else AllIcons.General.ZoomOut
        }

        list.addRMBMenuEntry("Delete") {
            listModel.remove(list.selectedIndex)
        }
        list.addKeyPressedListener {
            if (it?.keyChar?.toInt() == KeyEvent.VK_DELETE) {
                listModel.remove(list.selectedIndex)
            }
        }
        list.addMousePressedListener {
            list.selectedIndex = list.locationToIndex(it?.point)
            if (it?.clickCount == 2) {
                registerNewCommand()
            }
        }

        commandService.getOutputSubject().subscribe {
            listModel.add(0, it)
            list.selectedIndex = 0
            registerNewCommand()
        }
    }

    private fun registerNewCommand() {
        contentArea.text = ""
        if (this::lastSubscribe.isInitialized) {
            lastSubscribe.dispose()
        }
        lastSubscribe = listModel.get(list.selectedIndex).response
            .subscribe { msg ->
                contentArea.append(msg + "\n")
                contentArea.caretPosition = contentArea.document.length
            }
    }

}
