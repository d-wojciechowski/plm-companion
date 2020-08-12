package pl.dwojciechowski.ui.panel

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import pl.dwojciechowski.configuration.ProjectPluginConfiguration
import pl.dwojciechowski.i18n.PluginBundle.getMessage
import pl.dwojciechowski.model.ServerStatus
import pl.dwojciechowski.service.IdeControlService
import pl.dwojciechowski.service.RemoteService
import pl.dwojciechowski.service.StatusService
import pl.dwojciechowski.ui.PluginIcons
import pl.dwojciechowski.ui.component.EtchedTitleBorder
import pl.dwojciechowski.ui.dialog.DescribePropertyDialog
import pl.dwojciechowski.ui.dialog.PluginSettingsDialog
import javax.swing.JButton
import javax.swing.JPanel

class FatButtonPanel(private val project: Project) {

    private val config = ServiceManager.getService(project, ProjectPluginConfiguration::class.java)
    private val windchillService = ServiceManager.getService(project, RemoteService::class.java)
    private val statusService = ServiceManager.getService(project, StatusService::class.java)
    private val ideControlService = ServiceManager.getService(project, IdeControlService::class.java)

    lateinit var content: JPanel
    lateinit var statusPanel: JPanel
    lateinit var actionPanel: JPanel

    private lateinit var restartWindchillButton: JButton
    private lateinit var stopWindchillButton: JButton
    private lateinit var startWindchillButton: JButton
    private lateinit var configurationButton: JButton
    private lateinit var wncStatusButton: JButton
    private lateinit var xconfManagerButton: JButton
    private lateinit var describePropertyButton: JButton

    init {
        wncStatusButton.isContentAreaFilled = false
        wncStatusButton.isBorderPainted = false
        wncStatusButton.background = null
        wncStatusButton.isOpaque = false

        restartWindchillButton.addActionListener { ideControlService.withAutoOpen { windchillService.restartWnc() } }
        stopWindchillButton.addActionListener { ideControlService.withAutoOpen { windchillService.stopWnc() } }
        startWindchillButton.addActionListener { ideControlService.withAutoOpen { windchillService.startWnc() } }
        xconfManagerButton.addActionListener { ideControlService.withAutoOpen { windchillService.xconf() } }
        configurationButton.addActionListener { PluginSettingsDialog(project).show() }
        wncStatusButton.addActionListener { config.scanWindchill = !config.scanWindchill }
        describePropertyButton.addActionListener { DescribePropertyDialog(project).show() }

        statusService.getOutputSubject().subscribe {
            wncStatusButton.set(it)
            setEnableStateBasedOnStatus(it)
        }

        statusPanel.border = EtchedTitleBorder(getMessage("ui.mp.status"))
        actionPanel.border = EtchedTitleBorder(getMessage("ui.mp.actions"))
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
                    listOf(startWindchillButton, xconfManagerButton),
                    listOf(stopWindchillButton, restartWindchillButton)
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