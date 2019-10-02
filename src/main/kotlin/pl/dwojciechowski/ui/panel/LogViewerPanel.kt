package pl.dwojciechowski.ui.panel

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import io.grpc.ManagedChannel
import io.grpc.stub.StreamObserver
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.proto.Service
import pl.dwojciechowski.service.impl.LogViewerServiceImpl
import javax.swing.JPanel
import javax.swing.JTabbedPane
import javax.swing.JTextArea

class LogViewerPanel(private val project: Project) {

    lateinit var content: JPanel
    private lateinit var tabPane: JTabbedPane

    private lateinit var ms: JTextArea
    private lateinit var bms: JTextArea
    var channel: ManagedChannel? = null

    private val logService: LogViewerServiceImpl = ServiceManager.getService(project, LogViewerServiceImpl::class.java)
    private val config = ServiceManager.getService(project, PluginConfiguration::class.java)

    init {
        config.subjectLog
            .doOnNext { b -> toggleLogViewer(b) }
            .subscribe()
    }

    private fun toggleLogViewer(enable: Boolean) {
        if (enable) {
            val logsObserver = object : StreamObserver<Service.LogLine> {
                override fun onNext(value: Service.LogLine?) {
                    ms.append(value?.message)
                    ms.append("\r\n")
                    ms.caretPosition = ms.document.length
                }

                override fun onError(t: Throwable?) {
                    //todo wrong thread, needs to go to main with some kind of pipeline ->
                    // create UI service with error and message pipelines ? potentially move subjectLog there
//                    Messages.showErrorDialog(project, t?.toString(), "${t?.message}")
                }

                override fun onCompleted() {
                    ms.append("\r\r")
                    ms.append("DISCONNECTED!")
                    ms.append("\r\n")
                }
            }
            channel = logService.getLogFile(config, logsObserver)
        } else {
            channel?.shutdownNow()
        }
    }

}