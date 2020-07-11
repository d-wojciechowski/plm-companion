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
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.proto.files.LogLine
import pl.dwojciechowski.service.LogViewerService
import pl.dwojciechowski.ui.component.button.AutoScrollButton
import pl.dwojciechowski.ui.component.button.LineWrapButton
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

    private val config: PluginConfiguration = ServiceManager.getService(project, PluginConfiguration::class.java)
    private val logService: LogViewerService = ServiceManager.getService(project, LogViewerService::class.java)

    var parentContent: Content? = null

    lateinit var panel: JPanel

    private lateinit var logTextArea: JTextArea
    private lateinit var logInputChannel: Disposable

    private lateinit var startRestartButton: JButton
    private lateinit var stopButton: JButton
    private lateinit var clearButton: JButton
    private lateinit var autoScrollJButton: AutoScrollButton
    private lateinit var wrapLinesButton: LineWrapButton
    private lateinit var settingsJB: JButton

    private var isRunning = false

    init {
        this.add(panel)

        wrapLinesButton.link(config.wrapLogPane, logTextArea) {
            config.wrapLogPane = it
        }

        autoScrollJButton.link(config.logPanelAutoScroll, logTextArea) {
            config.logPanelAutoScroll = it
        }

        startRestartButton.icon = AllIcons.RunConfigurations.TestState.Run
        startRestartButton.addActionListener { GlobalScope.launch { startRestart() } }

        stopButton.addActionListener { stopLogViewer() }
        stopButton.icon = AllIcons.Actions.Suspend

        clearButton.addActionListener { logTextArea.text = "" }
        clearButton.icon = AllIcons.Actions.GC

        if (SourceEnum.CUSTOM == type) {
            settingsJB.icon = AllIcons.General.Settings
            startRestartButton.isEnabled = false
            settingsJB.addActionListener {
                val dialog = LogFileLocationDialog(project, customLogFileLocation,true)
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
        if (isRunning) stopLogViewer()
        isRunning = true
        startRestartButton.icon = AllIcons.Actions.Restart
        stopButton.isEnabled = true

        logTextArea.text = ""
        val onError: (Throwable) -> Unit = {
            logTextArea.append(it.message + "\n")
            stopLogViewer()
        }
        val onNext: (LogLine) -> Unit = {
            logTextArea.append(it.message + "\n")
            if (config.logPanelAutoScroll) {
                logTextArea.caretPosition = logTextArea.document.length
            }
        }
        logInputChannel = fileContentChannelFactory(onError, onNext)
    }

    private fun fileContentChannelFactory(onError: (Throwable) -> Unit, onNext: (LogLine) -> Unit): Disposable {
        return if (SourceEnum.CUSTOM == type) {
            logService.getCustomLogFile(customLogFileLocation, logsObserver = onNext, logsErrorObserver = onError)
        } else {
            logService.getLogFile(type, logsObserver = onNext, logsErrorObserver = onError)
        }
    }

    private fun stopLogViewer() {
        try {
            isRunning = false
            startRestartButton.icon = AllIcons.RunConfigurations.TestState.Run
            stopButton.isEnabled = false
            logInputChannel.dispose()
        } catch (t: Throwable) {
            ApplicationManager.getApplication().invokeLater {
                Messages.showErrorDialog(project, t.toString(), "${t.message}")
            }
        }
    }

}
