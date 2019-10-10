package pl.dwojciechowski.ui.panel

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import io.grpc.StatusRuntimeException
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.model.HttpStatusConfig
import pl.dwojciechowski.model.ServerStatus
import pl.dwojciechowski.proto.Service
import pl.dwojciechowski.service.HttpService
import pl.dwojciechowski.service.WncConnectorService
import pl.dwojciechowski.ui.PluginIcons
import pl.dwojciechowski.ui.WindchillNotification
import javax.swing.JButton
import javax.swing.JPanel

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

    private var previousStatus = ServerStatus.DOWN

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

    private fun JButton.addListener(function: () -> Service.Response) {
        this.addActionListener { executeAction(function, this) }
    }

    private fun executeAction(action: () -> Service.Response, button: JButton) {
        WindchillNotification.createNotification(project, "Started execution of ${button.name}", PluginIcons.OK)
        button.isEnabled = false
        Flowable.fromCallable(action)
            .subscribeOn(Schedulers.newThread())
            .subscribe(
                { handle(it) },
                { showMessageOnUIThread(it, button) },
                { button.isEnabled = true }
            )
    }

    private fun handle(response: Service.Response) {
        if (response.status == 200) {
            WindchillNotification.createNotification(
                project,
                "Action executed successfully",
                PluginIcons.OK
            )
        } else {
            WindchillNotification.createNotification(project, "Action FAILED", PluginIcons.KO)
        }
    }

    private fun showMessageOnUIThread(error: Throwable, button: JButton) {
        ApplicationManager.getApplication().invokeLater {
            if (error is StatusRuntimeException) {
                Messages.showMessageDialog(
                    project,
                    "Could not connect to windchill add-on, at specified host: ${config.hostname}\n${error.message}",
                    "Connection error", Messages.getErrorIcon()
                )
            } else {
                Messages.showMessageDialog(
                    project,
                    error.message,
                    "Connection error",
                    Messages.getErrorIcon()
                )
            }
        }
        button.isEnabled = true
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
