package pl.dominikw.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBarWidgetProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.dominikw.configuration.PluginConfiguration
import pl.dominikw.model.ServerStatus
import pl.dominikw.service.HttpService
import javax.swing.*

internal class WindchillWindowPanel(private val project: Project) : Disposable {

    private val config: PluginConfiguration = ServiceManager.getService(project, PluginConfiguration::class.java)

    lateinit var content: JPanel
    private lateinit var restartWindchillButton: JButton
    private lateinit var windchillStatusLabel: JLabel

    //CONFIG SECTION
    private lateinit var serverUrlTextField: JTextField
    private lateinit var lockSettings: JCheckBox
    private lateinit var shouldScanCheckbox: JCheckBox
    private lateinit var loginField: JTextField
    private lateinit var passwordField: JPasswordField
    private lateinit var refreshRateSpinner: JSpinner
    private lateinit var saveSettingsButton: JButton

    private var previousStatus = ServerStatus.DOWN

    init {
        initFromProperties()

        restartWindchillButton.isEnabled = false

        refreshRateSpinner.model = SpinnerNumberModel(1000, 500, 60_000, 100)
        refreshRateSpinner.editor = JSpinner.NumberEditor(refreshRateSpinner, "# ms")

        saveSettingsButton.addActionListener { saveConfig() }
        lockSettings.addActionListener { setSelectableConfig() }

        GlobalScope.launch {
            while (true) {
                if (shouldScanCheckbox.isSelected) scanServer() else windchillStatusLabel.set(ServerStatus.NOT_SCANNING)
                delay((refreshRateSpinner.value as Int).toLong())
            }
        }
    }

    private fun scanServer() {
        val url = serverUrlTextField.text
        val login = loginField.text
        val password = String(passwordField.password)
        val status = HttpService.getInstance().getStatus(url, login, password)
        if (status != previousStatus && status == ServerStatus.RUNNING) {
            WindchillNotification.serverOK(project)
        } else if (status != previousStatus && status != ServerStatus.RUNNING){
            WindchillNotification.serverKO(project)
        }
        previousStatus = status
        windchillStatusLabel.set(status)
    }

    private fun setSelectableConfig() {
        val isSelected = !lockSettings.isSelected
        serverUrlTextField.isEnabled = isSelected
        loginField.isEnabled = isSelected
        passwordField.isEnabled = isSelected
        refreshRateSpinner.isEnabled = isSelected
        saveSettingsButton.isEnabled = isSelected
    }

    private fun saveConfig() {
        config.url = serverUrlTextField.text
        config.login = loginField.text
        config.password = String(passwordField.password)
        config.preserveConfig = lockSettings.isSelected
        config.scanWindchill = shouldScanCheckbox.isSelected
        config.refreshRate = refreshRateSpinner.value as Int
    }

    private fun initFromProperties() {
        serverUrlTextField.text = config.url
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
