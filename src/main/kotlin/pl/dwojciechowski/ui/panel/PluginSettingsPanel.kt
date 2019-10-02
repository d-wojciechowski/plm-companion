package pl.dwojciechowski.ui.panel

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import org.picocontainer.Disposable
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.ui.WindchillNotification
import java.awt.Dimension
import java.awt.event.ActionEvent
import javax.swing.*

class PluginSettingsPanel(private val project: Project) : DialogWrapper(project), Disposable {

    private val config: PluginConfiguration = ServiceManager.getService(project, PluginConfiguration::class.java)

    lateinit var content: JPanel

    private lateinit var protocolCB: JComboBox<String>
    private lateinit var hostnameField: JTextField
    private lateinit var windchillRelativeTextField: JTextField
    private lateinit var portSpinner: JSpinner
    private lateinit var loginField: JTextField
    private lateinit var passwordField: JPasswordField
    private lateinit var refreshRateSpinner: JSpinner
    private lateinit var timeoutSpinner: JSpinner
    private lateinit var logFileLocation: JTextField

    fun createUIComponents() {
        hostnameField = JTextField("enter domain like \" google.com \"", 35)
        protocolCB = ComboBox(arrayOf("http", "https")).withFixedSize(Dimension(65, 5))

        portSpinner = JSpinner(SpinnerNumberModel(8080, 1, 9999, 1))
            .withFixedSize(Dimension(70, 5))
        portSpinner.editor = JSpinner.NumberEditor(portSpinner, ":#")
    }

    init {
        refreshRateSpinner.model = SpinnerNumberModel(1000, 500, Int.MAX_VALUE, 100)
        refreshRateSpinner.editor = JSpinner.NumberEditor(refreshRateSpinner, "# ms")
        timeoutSpinner.model = SpinnerNumberModel(5000, 500, Int.MAX_VALUE, 100)
        timeoutSpinner.editor = JSpinner.NumberEditor(timeoutSpinner, "# ms")

        initFromConfig()
        init()
    }

    override fun createCenterPanel() = content
    override fun dispose() = super.dispose()

    private fun initFromConfig() {
        protocolCB.selectedItem = config.protocol
        hostnameField.text = config.hostname
        windchillRelativeTextField.text = config.relativePath
        portSpinner.value = config.port

        loginField.text = config.login
        passwordField.text = config.password
        refreshRateSpinner.value = config.refreshRate
        timeoutSpinner.value = config.timeout
        logFileLocation.text = config.logFileLocation
    }

    private fun saveConfig() {
        config.protocol = protocolCB.selectedItem as String
        config.hostname = hostnameField.text
        config.relativePath = windchillRelativeTextField.text
        config.port = portSpinner.value as Int

        config.login = loginField.text
        config.password = String(passwordField.password)
        config.refreshRate = refreshRateSpinner.value as Int
        config.timeout = timeoutSpinner.value as Int
        config.logFileLocation = logFileLocation.text
    }


    override fun getOKAction(): Action =
        object : AbstractAction("OK") {
            override fun actionPerformed(e: ActionEvent?) {
                saveConfig()
                WindchillNotification.settingsSaved(project)
                dispose()
            }
        }

    override fun getCancelAction(): Action =
        object : AbstractAction("Cancel") {
            override fun actionPerformed(e: ActionEvent?) {
                dispose()
            }
        }

    private fun <T : JComponent> T.withFixedSize(dimension: Dimension): T {
        this.size = dimension
        this.preferredSize = dimension
        this.maximumSize = dimension
        return this
    }


}