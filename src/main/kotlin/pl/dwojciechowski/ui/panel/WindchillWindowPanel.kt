package pl.dwojciechowski.ui.panel

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.model.CommandConfig
import pl.dwojciechowski.model.HttpStatusConfig
import pl.dwojciechowski.model.ServerStatus
import pl.dwojciechowski.service.HttpService
import pl.dwojciechowski.service.WncConnectorService
import pl.dwojciechowski.ui.WindchillNotification
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JScrollPane

internal class WindchillWindowPanel(private val project: Project) {

    private val config = ServiceManager.getService(project, PluginConfiguration::class.java)
    private val windchillService = ServiceManager.getService(project, WncConnectorService::class.java)

    lateinit var content: JPanel
    private lateinit var restartWindchillButton: JButton
    private lateinit var stopWindchillButton: JButton
    private lateinit var startWindchillButton: JButton
    private lateinit var configurationButton: JButton
    private lateinit var wncStatusButton: JButton
    private lateinit var xconfManagerButton: JButton

    //Log Section
    private lateinit var logsSP: JScrollPane
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
        xconfManagerButton.addActionListener(wrapWithErrorDialog { windchillService.xconf(CommandConfig(config)) })
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
        config.subjectLog.onNext(showLogsCB.isSelected)
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
