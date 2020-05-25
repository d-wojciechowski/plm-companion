package pl.dwojciechowski.ui.panel

import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.content.Content
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.dwojciechowski.service.LogViewerService
import pl.dwojciechowski.ui.dialog.LogFileLocationDialog
import reactor.core.Disposable
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTextArea
import pl.dwojciechowski.proto.files.LogFileLocation.Source as SourceEnum

class LogViewerPanel(
    private val project: Project,
    private val type: SourceEnum,
    private var customLogFileLocation: String = ""
) : SimpleToolWindowPanel(false, true) {

    private val logService: LogViewerService = ServiceManager.getService(project, LogViewerService::class.java)

    lateinit var panel: JPanel

    private lateinit var textArea: JTextArea
    private lateinit var channel: Disposable
    private lateinit var startRestartButton: JButton
    private lateinit var stopButton: JButton
    private lateinit var clearButton: JButton
    private lateinit var settingsJB: JButton
    var parentContent: Content? = null

    private var status = false

    init {
        this.add(panel)

        startRestartButton.icon = AllIcons.RunConfigurations.TestState.Run
        startRestartButton.addActionListener { GlobalScope.launch { startRestart() } }

        stopButton.addActionListener { stopLogViewer() }
        stopButton.icon = AllIcons.Actions.Suspend

        clearButton.addActionListener { textArea.text = "" }
        clearButton.icon = AllIcons.Actions.GC

        if (SourceEnum.CUSTOM == type) {
            settingsJB.icon = AllIcons.General.Settings
            startRestartButton.isEnabled = false
            settingsJB.addActionListener {
                val dialog = LogFileLocationDialog(project, customLogFileLocation)
                if (dialog.showAndGet()) {
                    parentContent?.displayName = dialog.result
                    customLogFileLocation = dialog.result
                    startRestartButton.isEnabled = true
                }
            }
            parentContent?.displayName = customLogFileLocation
            startRestart()
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
        val logsError: (Throwable) -> Unit = {
            textArea.append(it.message + "\n")
            stopLogViewer()
        }
        if (SourceEnum.CUSTOM == type) {
            channel = logService.getCustomLogFile(
                customLogFileLocation,
                logsObserver = {
                    textArea.append(it.message + "\n")
                    textArea.caretPosition = textArea.document.length
                },
                logsErrorObserver = logsError
            )
        } else {
            channel = logService.getLogFile(
                type,
                logsObserver = {
                    textArea.append(it.message + "\n")
                    textArea.caretPosition = textArea.document.length
                },
                logsErrorObserver = logsError
            )
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
