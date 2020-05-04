package pl.dwojciechowski.ui.panel

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.model.HttpStatusConfig
import pl.dwojciechowski.model.ServerStatus
import pl.dwojciechowski.service.HttpService
import pl.dwojciechowski.service.RemoteService
import pl.dwojciechowski.ui.PLMPluginNotification
import pl.dwojciechowski.ui.PluginIcons
import pl.dwojciechowski.ui.dialog.PluginSettingsDialog
import javax.swing.JButton
import javax.swing.JPanel

class FatButtonPanel(private val project: Project) {

    private val config = ServiceManager.getService(project, PluginConfiguration::class.java)
    private val windchillService = ServiceManager.getService(project, RemoteService::class.java)
    private val httpService = ServiceManager.getService(project, HttpService::class.java)

    lateinit var content: JPanel

    private lateinit var restartWindchillButton: JButton
    private lateinit var stopWindchillButton: JButton
    private lateinit var startWindchillButton: JButton
    private lateinit var configurationButton: JButton
    private lateinit var wncStatusButton: JButton
    private lateinit var xconfManagerButton: JButton

    private var previousStatus = ServerStatus.DOWN

    init {
        wncStatusButton.isContentAreaFilled = false
        wncStatusButton.isBorderPainted = false
        wncStatusButton.background = null
        wncStatusButton.isOpaque = false

        restartWindchillButton.addActionListener { windchillService.restartWnc() }
        stopWindchillButton.addActionListener { windchillService.stopWnc() }
        startWindchillButton.addActionListener { windchillService.startWnc() }
        xconfManagerButton.addActionListener { windchillService.xconf() }
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

    private fun scanServer() {
        val status = httpService.getStatus(HttpStatusConfig(config))
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