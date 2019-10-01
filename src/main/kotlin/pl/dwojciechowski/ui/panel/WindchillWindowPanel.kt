package pl.dwojciechowski.ui.panel

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.model.CommandConfig
import pl.dwojciechowski.model.HttpStatusConfig
import pl.dwojciechowski.model.ServerStatus
import pl.dwojciechowski.proto.Service
import pl.dwojciechowski.service.HttpService
import pl.dwojciechowski.service.WncConnectorService
import pl.dwojciechowski.service.impl.LogViewerServiceImpl
import pl.dwojciechowski.ui.WindchillNotification
import java.awt.event.ActionListener
import javax.swing.*

internal class WindchillWindowPanel(private val project: Project) {

    private val config = ServiceManager.getService(project, PluginConfiguration::class.java)
    private val windchillService = ServiceManager.getService(project, WncConnectorService::class.java)
    private val logService: LogViewerServiceImpl = ServiceManager.getService(project, LogViewerServiceImpl::class.java)

    lateinit var content: JPanel
    private lateinit var restartWindchillButton: JButton
    private lateinit var stopWindchillButton: JButton
    private lateinit var startWindchillButton: JButton
    private lateinit var configurationButton: JButton
    private lateinit var wncStatusButton: JButton

    //Log Section
    private lateinit var logsSP: JScrollPane
    private lateinit var logViewerTA: JTextArea
    private lateinit var showLogsCB: JCheckBox

    private var previousStatus = ServerStatus.DOWN

    init {
        wncStatusButton.isContentAreaFilled = false
        wncStatusButton.isBorderPainted = false
        wncStatusButton.background = null
        wncStatusButton.isOpaque = false

        restartWindchillButton.addActionListener(wrapWithErrorDialog { windchillService.restartWnc(CommandConfig(config)) })
        stopWindchillButton.addActionListener(wrapWithErrorDialog { windchillService.stopWnc(CommandConfig(config)) })
        startWindchillButton.addActionListener(wrapWithErrorDialog { windchillService.startWnc(CommandConfig(config)) })
        configurationButton.addActionListener { PluginSettingsPanel(project).show() }
        wncStatusButton.addActionListener {
            config.scanWindchill = !config.scanWindchill
            if (config.scanWindchill) scanServer() else wncStatusButton.set(ServerStatus.NOT_SCANNING)
        }
        showLogsCB.addActionListener { toggleLogViewer() }

        GlobalScope.launch {
            while (true) {
                if (config.scanWindchill) scanServer() else wncStatusButton.set(ServerStatus.NOT_SCANNING)
                delay(config.refreshRate.toLong())
            }
        }
    }

    private fun toggleLogViewer() {
        logViewerTA.isEnabled = logViewerTA.isEnabled.not()
        logViewerTA.isVisible = logViewerTA.isVisible.not()
        if (logViewerTA.isVisible) {
            val logsObserver = object : StreamObserver<Service.LogLine> {
                override fun onNext(value: Service.LogLine?) {
                    logViewerTA.append(value?.message)
                    logViewerTA.append("\r\n")
                    logViewerTA.caretPosition = logViewerTA.document.length
                }

                override fun onError(t: Throwable?) {
                    Messages.showErrorDialog(project, t?.toString(), "${t?.message}")
                }

                override fun onCompleted() {
                    logViewerTA.append("\r\r")
                    logViewerTA.append("DISCONNECTED!")
                    logViewerTA.append("\r\n")
                }
            }
            logService.getLogFile(config, logsObserver)
        }
    }

    private fun wrapWithErrorDialog(action: () -> Unit): ActionListener? {
        return ActionListener {
            try {
                action.invoke()
            } catch (e: StatusRuntimeException) {
                Messages.showMessageDialog(
                    "Could not connect to windchill add-on, at specified host: ${config.hostname}",
                    "Connection error", Messages.getErrorIcon()
                )
            } catch (e: Exception) {
                Messages.showMessageDialog(e.message, "Connection Error", Messages.getErrorIcon())
            }
        }
    }

    private fun scanServer() {
        val status = HttpService.getInstance().getStatus(HttpStatusConfig(config))
        when (status) {
            previousStatus -> Unit
            ServerStatus.STARTING -> WindchillNotification.apacheOK(project)
            ServerStatus.RUNNING -> WindchillNotification.serverOK(project)
            else -> WindchillNotification.serverKO(project)
        }
        previousStatus = status
        wncStatusButton.set(status)
    }

    private fun JButton.set(status: ServerStatus) {
        this.icon = status.icon
        this.text = status.label
    }

}
