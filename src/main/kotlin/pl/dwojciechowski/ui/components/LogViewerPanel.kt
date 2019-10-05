package pl.dwojciechowski.ui.components

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.SimpleToolWindowPanel
import io.grpc.ManagedChannel
import io.grpc.stub.StreamObserver
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.proto.Service
import pl.dwojciechowski.service.LogViewerService
import javax.swing.JScrollPane
import javax.swing.JTextArea

class LogViewerPanel(private val project: Project) : SimpleToolWindowPanel(false, true) {

    private val logService: LogViewerService = ServiceManager.getService(project, LogViewerService::class.java)
    private val config = ServiceManager.getService(project, PluginConfiguration::class.java)

    private var textArea: JTextArea
    private lateinit var channel: ManagedChannel

    init {
        autoscrolls = true
        textArea = JTextArea()
        textArea.isEditable = false

        this.add(JScrollPane(textArea))

        config.subjectLog
            .doOnNext { b -> toggleLogViewer(b) }
            .subscribe()
    }

    private fun toggleLogViewer(enable: Boolean) {
        if (enable) {
            textArea.text = ""
            val logsObserver = object : StreamObserver<Service.LogLine> {
                override fun onNext(value: Service.LogLine?) {
                    textArea.append(value?.message)
                    textArea.append("\r\n")
                    textArea.caretPosition = textArea.document.length
                }

                override fun onError(t: Throwable?) {
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
            channel = logService.getLogFile(config, logsObserver)
        } else {
            channel.shutdown()
        }
    }

}