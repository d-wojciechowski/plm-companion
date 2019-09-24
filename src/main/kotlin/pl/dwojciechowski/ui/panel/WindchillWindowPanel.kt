package pl.dwojciechowski.ui.panel

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.dwojciechowski.ui.WindchillNotification
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.model.ServerStatus
import pl.dwojciechowski.service.HttpService
import pl.dwojciechowski.service.WncConnectorService
import java.awt.event.ActionListener
import javax.swing.*

internal class WindchillWindowPanel(private val project: Project) : Disposable {

    private val config: PluginConfiguration = ServiceManager.getService(project, PluginConfiguration::class.java)
    private val windchillService: WncConnectorService =
        ServiceManager.getService(project, WncConnectorService::class.java)

    lateinit var content: JPanel
    private lateinit var restartWindchillButton: JButton
    private lateinit var stopWindchillButton: JButton
    private lateinit var startWindchillButton: JButton
    private lateinit var windchillStatusLabel: JLabel

    //CONFIG SECTION
    private lateinit var protocolCB: JComboBox<String>
    private lateinit var hostnameField: JTextField
    private lateinit var portSpinner: JSpinner
    private lateinit var lockSettings: JCheckBox
    private lateinit var shouldScanCheckbox: JCheckBox
    private lateinit var loginField: JTextField
    private lateinit var passwordField: JPasswordField
    private lateinit var refreshRateSpinner: JSpinner
    private lateinit var saveSettingsButton: JButton

    private var previousStatus = ServerStatus.DOWN

    init {
        refreshRateSpinner.model = SpinnerNumberModel(1000, 500, 60_000, 100)
        refreshRateSpinner.editor = JSpinner.NumberEditor(refreshRateSpinner, "# ms")

        portSpinner.model = SpinnerNumberModel(8080, 1, 9999, 1)
        portSpinner.editor = JSpinner.NumberEditor(portSpinner, ":#")

        initFromProperties()

        restartWindchillButton.addActionListener(wrapWithErrorDialog { windchillService.restartWnc(hostnameField.text) })
        stopWindchillButton.addActionListener(wrapWithErrorDialog { windchillService.stopWnc(hostnameField.text) })
        startWindchillButton.addActionListener(wrapWithErrorDialog { windchillService.startWnc(hostnameField.text) })
        saveSettingsButton.addActionListener { saveConfig() }
        lockSettings.addActionListener { setSelectableConfig() }

        GlobalScope.launch {
            while (true) {
                if (shouldScanCheckbox.isSelected) scanServer() else windchillStatusLabel.set(ServerStatus.NOT_SCANNING)
                delay((refreshRateSpinner.value as Int).toLong())
            }
        }
    }

    private fun wrapWithErrorDialog(action: () -> Unit): ActionListener? {
        return ActionListener {
            try {
                action.invoke()
            } catch (e: StatusRuntimeException) {
                Messages.showMessageDialog(
                    "Could not connect to windchill addon, at specified host: ${hostnameField.text}",
                    "Connection error", Messages.getErrorIcon()
                )
            } catch (e: Exception) {
                Messages.showMessageDialog(e.message, "Connection Error", Messages.getErrorIcon())
            }
        }
    }


    private fun scanServer() {
        val url = "${protocolCB.selectedItem as String}://${hostnameField.text}:${portSpinner.value}/Windchill/app"
        val login = loginField.text
        val password = String(passwordField.password)
        val status = HttpService.getInstance().getStatus(url, login, password)
        if (status != previousStatus && status == ServerStatus.RUNNING) {
            WindchillNotification.serverOK(project)
        } else if (status != previousStatus && status != ServerStatus.RUNNING) {
            WindchillNotification.serverKO(project)
        }
        previousStatus = status
        windchillStatusLabel.set(status)
    }

    private fun setSelectableConfig() {
        val isSelected = !lockSettings.isSelected
        protocolCB.isEnabled = isSelected
        portSpinner.isEnabled = isSelected
        hostnameField.isEnabled = isSelected
        loginField.isEnabled = isSelected
        passwordField.isEnabled = isSelected
        refreshRateSpinner.isEnabled = isSelected
    }

    private fun saveConfig() {
        config.protocol = protocolCB.selectedItem as String
        config.hostname = hostnameField.text
        config.port = portSpinner.value as Int

        config.login = loginField.text
        config.password = String(passwordField.password)
        config.preserveConfig = lockSettings.isSelected
        config.scanWindchill = shouldScanCheckbox.isSelected
        config.refreshRate = refreshRateSpinner.value as Int

        WindchillNotification.settingsSaved(project)
    }

    private fun initFromProperties() {
        hostnameField.text = config.hostname
        protocolCB.selectedItem = config.protocol
        portSpinner.value = config.port
        loginField.text = config.login
        passwordField.text = config.password
        lockSettings.isSelected = config.preserveConfig
        shouldScanCheckbox.isSelected = config.scanWindchill
        refreshRateSpinner.value = config.refreshRate
        setSelectableConfig()
    }

    override fun dispose() = saveConfig()

    private fun JLabel.set(status: ServerStatus) {
        this.icon = status.icon
        this.text = status.label
    }

}
