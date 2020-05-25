package pl.dwojciechowski.ui.panel

import com.intellij.execution.ui.ExecutionConsole
import com.intellij.icons.AllIcons
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import io.reactivex.rxjava3.disposables.Disposable
import pl.dwojciechowski.model.CommandBean
import pl.dwojciechowski.model.ExecutionStatus
import pl.dwojciechowski.service.RemoteService
import pl.dwojciechowski.ui.component.CommandList
import java.awt.event.KeyEvent
import javax.swing.DefaultListModel
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTextArea

class CommandLogPanel(project: Project) : SimpleToolWindowPanel(false, true), ExecutionConsole {

    private val commandService: RemoteService = ServiceManager.getService(project, RemoteService::class.java)

    lateinit var panel: JPanel

    private lateinit var contentArea: JTextArea
    private lateinit var clearButton: JButton
    private lateinit var autoScrollJButton: JButton

    private var autoScroll = true
    private lateinit var lastSubscribe: Disposable
    private var subscribes = HashMap<Int, reactor.core.Disposable>()

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

        list.init()

        commandService.getOutputSubject().subscribe {
            listModel.add(0, it)
            list.selectedIndex = 0
            registerNewCommand()
        }
    }

    private fun CommandList.init() {
        addRMBMenuEntry("Delete") {
            listModel.remove(selectedIndex)
            removeSubscription()
        }
        addRMBMenuEntry("Stop") {
            listModel.selected().status = ExecutionStatus.STOPPED
            removeSubscription()
        }
        addKeyPressedListener {
            if (it?.keyChar?.toInt() == KeyEvent.VK_DELETE) {
                listModel.remove(selectedIndex)
            }
        }
        addMousePressedListener {
            selectedIndex = locationToIndex(it?.point)
            if (it?.clickCount == 2) {
                registerNewCommand()
            }
        }
    }

    private fun registerNewCommand() {
        contentArea.text = ""
        stopListeningToCurrentCommand()
        lastSubscribe = listModel.selected().response
            .subscribe { msg ->
                contentArea.append(msg + "\n")
                if (autoScroll) {
                    contentArea.caretPosition = contentArea.document.length
                }
            }
        subscribes[list.selectedIndex] = listModel.selected().actualSubscription
    }

    private fun removeSubscription() {
        subscribes[list.selectedIndex]?.dispose()
        subscribes.remove(list.selectedIndex)
        stopListeningToCurrentCommand()
    }

    private fun stopListeningToCurrentCommand() {
        if (this::lastSubscribe.isInitialized) {
            lastSubscribe.dispose()
        }
    }

    private fun DefaultListModel<CommandBean>.selected() = this.get(list.selectedIndex)

    override fun getPreferredFocusableComponent() = panel

    override fun getComponent() = panel

    override fun dispose() {}

}
