package pl.dwojciechowski.ui.panel

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.model.ServerStatus
import pl.dwojciechowski.service.RemoteService
import pl.dwojciechowski.service.StatusService
import pl.dwojciechowski.ui.PluginIcons
import pl.dwojciechowski.ui.dialog.PluginSettingsDialog
import javax.swing.JButton
import javax.swing.JPanel

class FatButtonPanel(private val project: Project) {

    private val config = ServiceManager.getService(project, PluginConfiguration::class.java)
    private val windchillService = ServiceManager.getService(project, RemoteService::class.java)
    private val statusService = ServiceManager.getService(project, StatusService::class.java)

    lateinit var content: JPanel

    private lateinit var restartWindchillButton: JButton
    private lateinit var stopWindchillButton: JButton
    private lateinit var startWindchillButton: JButton
    private lateinit var configurationButton: JButton
    private lateinit var wncStatusButton: JButton
    private lateinit var xconfManagerButton: JButton

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
        wncStatusButton.addActionListener { config.scanWindchill = !config.scanWindchill }

        statusService.getOutputSubject().subscribe {
            wncStatusButton.set(it)
            setEnableStateBasedOnStatus(it)
        }
    }

    private fun setEnableStateBasedOnStatus(it: ServerStatus?) {
        if (config.statusControlled) {
            when (it) {
                ServerStatus.NOT_SCANNING -> set(
                    listOf(stopWindchillButton, restartWindchillButton, xconfManagerButton, startWindchillButton),
                    listOf()
                )
                ServerStatus.RUNNING -> set(
                    listOf(stopWindchillButton, restartWindchillButton, xconfManagerButton),
                    listOf(startWindchillButton)
                )
                else -> set(
                    listOf(startWindchillButton),
                    listOf(stopWindchillButton, restartWindchillButton, xconfManagerButton)
                )
            }
        } else {
            set(listOf(stopWindchillButton, restartWindchillButton, xconfManagerButton, startWindchillButton))
        }
    }

    private fun JButton.set(status: ServerStatus) {
        this.icon = PluginIcons.scaleToSize(status.icon, 20)
        this.text = status.label
    }

    private fun set(toEnable: List<JButton>, toDisable: List<JButton> = listOf()) {
        toEnable.forEach { it.isEnabled = true }
        toDisable.forEach { it.isEnabled = false }
    }

}