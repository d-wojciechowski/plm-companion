package pl.dwojciechowski.ui.panel

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.model.ServerStatus
import pl.dwojciechowski.service.HttpService
import pl.dwojciechowski.service.WncConnectorService
import pl.dwojciechowski.ui.WindchillNotification
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

internal class WindchillWindowPanel(private val project: Project){

    private val config = ServiceManager.getService(project, PluginConfiguration::class.java)
    private val windchillService = ServiceManager.getService(project, WncConnectorService::class.java)

    lateinit var content: JPanel
    private lateinit var restartWindchillButton: JButton
    private lateinit var stopWindchillButton: JButton
    private lateinit var startWindchillButton: JButton
    private lateinit var configurationButton: JButton
    private lateinit var windchillStatusLabel: JLabel

    private var previousStatus = ServerStatus.DOWN

    init {
        restartWindchillButton.addActionListener(wrapWithErrorDialog { windchillService.restartWnc(config.hostname) })
        stopWindchillButton.addActionListener(wrapWithErrorDialog { windchillService.stopWnc(config.hostname) })
        startWindchillButton.addActionListener(wrapWithErrorDialog { windchillService.startWnc(config.hostname) })
        configurationButton.addActionListener { PluginSettingsPanel(project).show() }

        GlobalScope.launch {
            while (true) {
                if (config.scanWindchill) scanServer() else windchillStatusLabel.set(ServerStatus.NOT_SCANNING)
                delay(config.refreshRate.toLong())
            }
        }
    }

    private fun wrapWithErrorDialog(action: () -> Unit): ActionListener? {
        return ActionListener {
            try {
                action.invoke()
            } catch (e: StatusRuntimeException) {
                Messages.showMessageDialog(
                    "Could not connect to windchill addon, at specified host: ${config.hostname}",
                    "Connection error", Messages.getErrorIcon()
                )
            } catch (e: Exception) {
                Messages.showMessageDialog(e.message, "Connection Error", Messages.getErrorIcon())
            }
        }
    }

    private fun scanServer() {
        val url = "${config.protocol}://${config.hostname}:${config.port}${config.relativePath}"
        val status = HttpService.getInstance().getStatus(url, config.login, config.password)
        if (status != previousStatus && status == ServerStatus.RUNNING) {
            WindchillNotification.serverOK(project)
        } else if (status != previousStatus && status != ServerStatus.RUNNING) {
            WindchillNotification.serverKO(project)
        }
        previousStatus = status
        windchillStatusLabel.set(status)
    }

    private fun JLabel.set(status: ServerStatus) {
        this.icon = status.icon
        this.text = status.label
    }

}
