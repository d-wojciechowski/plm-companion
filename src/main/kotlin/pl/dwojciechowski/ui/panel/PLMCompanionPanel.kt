package pl.dwojciechowski.ui.panel

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.model.HttpStatusConfig
import pl.dwojciechowski.model.ServerStatus
import pl.dwojciechowski.proto.commands.Response
import pl.dwojciechowski.service.ActionExecutor
import pl.dwojciechowski.service.HttpService
import pl.dwojciechowski.service.WncConnectorService
import pl.dwojciechowski.ui.PLMPluginNotification
import pl.dwojciechowski.ui.PluginIcons
import javax.swing.JButton
import javax.swing.JPanel

internal class PLMCompanionPanel(private val project: Project) {

    private val config = ServiceManager.getService(project, PluginConfiguration::class.java)
    private val windchillService = ServiceManager.getService(project, WncConnectorService::class.java)
    private val actionExecutor = ServiceManager.getService(project, ActionExecutor::class.java)

    lateinit var content: JPanel
    private lateinit var customCommandPanel: JPanel
    private lateinit var restartWindchillButton: JButton
    private lateinit var stopWindchillButton: JButton
    private lateinit var startWindchillButton: JButton
    private lateinit var configurationButton: JButton
    private lateinit var wncStatusButton: JButton
    private lateinit var xconfManagerButton: JButton

    private var previousStatus = ServerStatus.DOWN

    fun createUIComponents() {
        customCommandPanel = CommandSubPanel(project).content
    }

    init {
        wncStatusButton.isContentAreaFilled = false
        wncStatusButton.isBorderPainted = false
        wncStatusButton.background = null
        wncStatusButton.isOpaque = false

        restartWindchillButton.addListener { windchillService.restartWnc() }
        stopWindchillButton.addListener { windchillService.stopWnc() }
        startWindchillButton.addListener { windchillService.startWnc() }
        xconfManagerButton.addListener { windchillService.xconf() }
        configurationButton.addActionListener { PluginSettingsDialog(project).show() }
        wncStatusButton.addActionListener {
            config.scanWindchill = !config.scanWindchill
            if (config.scanWindchill) scanServer() else wncStatusButton.set(ServerStatus.NOT_SCANNING)
        }

        GlobalScope.launch {
            while (true) {
                if (config.scanWindchill) scanServer() else wncStatusButton.set(ServerStatus.NOT_SCANNING)
                delay(config.refreshRate.toLong())
            }
        }
    }

    private fun JButton.addListener(function: () -> Response) {
        this.addActionListener { actionExecutor.executeAction(this, function) }
    }

    private fun scanServer() {
        val status = HttpService.getInstance().getStatus(HttpStatusConfig(config))
        when (status) {
            previousStatus -> Unit
            ServerStatus.AVAILABLE -> PLMPluginNotification.apacheOK(project)
            ServerStatus.RUNNING -> PLMPluginNotification.serverOK(project)
            else -> PLMPluginNotification.serverKO(project)
        }
        previousStatus = status
        wncStatusButton.set(status)
    }

    private fun JButton.set(status: ServerStatus) {
        this.icon = PluginIcons.scaleToSize(status.icon, 20)
        this.text = status.label
    }

}
