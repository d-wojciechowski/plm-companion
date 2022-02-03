package pl.dwojciechowski.ui.panel

import com.intellij.execution.ui.ExecutionConsole
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import io.reactivex.rxjava3.disposables.Disposable
import pl.dwojciechowski.configuration.ProjectPluginConfiguration
import pl.dwojciechowski.i18n.PluginBundle.getMessage
import pl.dwojciechowski.model.CommandBean
import pl.dwojciechowski.model.ExecutionStatus
import pl.dwojciechowski.service.RemoteService
import pl.dwojciechowski.ui.component.CommandList
import pl.dwojciechowski.ui.component.button.AutoScrollButton
import pl.dwojciechowski.ui.component.button.LineWrapButton
import java.awt.event.KeyEvent
import javax.swing.DefaultListModel
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTextArea

class CommandLogPanel(project: Project) : SimpleToolWindowPanel(false, true), ExecutionConsole {

    private val config = project.getService(ProjectPluginConfiguration::class.java)
    private val commandService: RemoteService = project.getService(RemoteService::class.java)

    lateinit var panel: JPanel

    private lateinit var contentArea: JTextArea
    private lateinit var clearButton: JButton
    private lateinit var stopButton: JButton
    private lateinit var autoScrollJButton: AutoScrollButton
    private lateinit var wrapLinesButton: LineWrapButton
    private lateinit var rerunButton: JButton

    private lateinit var textFieldSubscription: Disposable
    private var subscriptions = HashMap<Int, reactor.core.Disposable>()

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

        stopButton.addActionListener {
            listModel.selected()?.status = ExecutionStatus.STOPPED
            removeSubscription()
        }
        stopButton.icon = AllIcons.Actions.Suspend

        rerunButton.addActionListener {
            rerunSelectedCommand()
        }
        rerunButton.icon = AllIcons.Actions.Restart

        list.addListSelectionListener {
            rerunButton.isEnabled = listModel.selected() != null
            stopButton.isEnabled = (listModel.selected()?.status == ExecutionStatus.RUNNING)
        }

        wrapLinesButton.link(config.wrapCommandPane, contentArea) {
            config.wrapCommandPane = it
        }

        autoScrollJButton.link(config.commandAutoScroll, contentArea) {
            config.commandAutoScroll = it
        }

        list.init()

        commandService.getOutputSubject().subscribe {
            listModel.add(0, it)
            list.selectedIndex = 0
            registerNewCommand()
        }
    }

    private fun rerunSelectedCommand() {
        listModel.selected()?.let {
            commandService.executeStreaming(it.clone())
        }
    }

    private fun CommandList.init() {
        addRMBMenuEntry(getMessage("ui.clp.rmb.rerun")) {
            rerunSelectedCommand()
        }
        addRMBMenuEntry(getMessage("ui.clp.rmb.delete")) {
            listModel.remove(selectedIndex)
            removeSubscription()
        }
        addRMBMenuEntry(getMessage("ui.clp.rmb.stop")) {
            listModel.selected()?.status = ExecutionStatus.STOPPED
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
        textFieldSubscription = getTextFieldSubscription()
        subscriptions[list.selectedIndex] = listModel.selected()?.actualSubscription ?: reactor.core.Disposable {}
    }

    private fun getTextFieldSubscription(): Disposable {
        return listModel.selected()?.response
            ?.subscribe { msg ->
                contentArea.append(msg + "\n")
                if (config.commandAutoScroll) {
                    contentArea.caretPosition = contentArea.document.length
                }
            } ?: Disposable.empty()
    }

    private fun removeSubscription() {
        subscriptions[list.selectedIndex]?.dispose()
        subscriptions.remove(list.selectedIndex)
        stopListeningToCurrentCommand()
    }

    private fun stopListeningToCurrentCommand() {
        if (this::textFieldSubscription.isInitialized) {
            textFieldSubscription.dispose()
        }
    }

    private fun DefaultListModel<CommandBean>.selected(): CommandBean? {
        if (list.selectedIndex >= 0) {
            return this.get(list.selectedIndex)
        }
        return null
    }

    override fun getPreferredFocusableComponent() = panel

    override fun getComponent() = panel

    override fun dispose() {}

}
