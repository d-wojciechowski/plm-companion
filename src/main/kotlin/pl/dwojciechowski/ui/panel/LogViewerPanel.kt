package pl.dwojciechowski.ui.panel

import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.content.Content
import io.grpc.ManagedChannel
import io.grpc.stub.StreamObserver
import io.reactivex.subjects.PublishSubject
import pl.dwojciechowski.proto.Service
import pl.dwojciechowski.service.LogViewerService
import pl.dwojciechowski.ui.dialog.LogFileLocationDialog
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTextArea
import pl.dwojciechowski.proto.Service.LogFileLocation.Source as SourceEnum

class LogViewerPanel(
    private val project: Project,
    private val type: SourceEnum
) : SimpleToolWindowPanel(false, true) {

    private val logService: LogViewerService = ServiceManager.getService(project, LogViewerService::class.java)

    lateinit var panel: JPanel

    private lateinit var textArea: JTextArea
    private lateinit var channel: ManagedChannel
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
            settingsJB.addActionListener { LogFileLocationDialog(project, logLocation).show() }
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
        channel = if (SourceEnum.CUSTOM == type) {
            logService.getCustomLogFile(customLogFileLocation, logsObserver)
        } else {
            logService.getLogFile(type, logsObserver)
        }
    }

    private fun stopLogViewer() {
        status = false
        startRestartButton.icon = AllIcons.RunConfigurations.TestState.Run
        stopButton.isEnabled = false
        channel.shutdown()
    }


    private val logsObserver = object : StreamObserver<Service.LogLine> {
        override fun onNext(value: Service.LogLine?) {
            textArea.append(value?.message)
            textArea.append("\r\n")
            textArea.caretPosition = textArea.document.length
        }

        override fun onError(t: Throwable?) {
            stopLogViewer()
            ApplicationManager.getApplication().invokeLater {
                Messages.showErrorDialog(project, t?.toString(), "${t?.message}")
            }
        }

        override fun onCompleted() {
            textArea.append("\r\r")
            textArea.append("DISCONNECTED!")
            textArea.append("\r\n")
        }
    }

}