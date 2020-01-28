package pl.dwojciechowski.ui.panel

import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.content.Content
import io.reactivex.rxjava3.subjects.PublishSubject
import pl.dwojciechowski.service.LogViewerService
import pl.dwojciechowski.ui.dialog.LogFileLocationDialog
import reactor.core.Disposable
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTextArea
import pl.dwojciechowski.proto.files.LogFileLocation.Source as SourceEnum

class LogViewerPanel(
    private val project: Project,
    private val type: SourceEnum
) : SimpleToolWindowPanel(false, true) {

    private val logService: LogViewerService = ServiceManager.getService(project, LogViewerService::class.java)

    lateinit var panel: JPanel

    private lateinit var textArea: JTextArea
    private lateinit var channel: Disposable
    private lateinit var startRestartButton: JButton
    private lateinit var stopButton: JButton
    private lateinit var clearButton: JButton
    private lateinit var settingsJB: JButton
    var customLogFileLocation = ""
    var parentContent: Content? = null

    private var status = false
    var logLocation = PublishSubject.create<String>()

    init {
        this.add(panel)

        startRestartButton.icon = AllIcons.RunConfigurations.TestState.Run
        startRestartButton.addActionListener { startRestart() }

        stopButton.addActionListener { stopLogViewer() }
        stopButton.icon = AllIcons.Actions.Suspend

        clearButton.addActionListener { textArea.text = "" }
        clearButton.icon = AllIcons.Actions.GC

        if (SourceEnum.CUSTOM == type) {
            settingsJB.icon = AllIcons.General.Settings
            startRestartButton.isEnabled = false
            settingsJB.addActionListener { LogFileLocationDialog(project, logLocation, customLogFileLocation).show() }
            logLocation.subscribe {
                parentContent?.displayName = it
                customLogFileLocation = it
                startRestartButton.isEnabled = true
            }
        } else {
            settingsJB.isVisible = false
        }
    }

    private fun startRestart() {
        if (status) stopLogViewer()
        status = true
        startRestartButton.icon = AllIcons.Actions.Restart
        stopButton.isEnabled = true

        textArea.text = ""
        if (SourceEnum.CUSTOM == type) {
            channel = logService.getCustomLogFile(customLogFileLocation) {
                textArea.append(it.message + "\n")
                textArea.caretPosition = textArea.document.length
            }
        } else {
            channel = logService.getLogFile(type) {
                textArea.append(it.message + "\n")
                textArea.caretPosition = textArea.document.length
            }
        }
    }

    private fun stopLogViewer() {
        try {
            status = false
            startRestartButton.icon = AllIcons.RunConfigurations.TestState.Run
            stopButton.isEnabled = false
            channel.dispose()
        } catch (t: Throwable) {
            ApplicationManager.getApplication().invokeLater {
                Messages.showErrorDialog(project, t.toString(), "${t.message}")
            }
        }
    }
}
