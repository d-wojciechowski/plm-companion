package pl.dwojciechowski.ui.dialog

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.util.containers.toArray
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.i18n.PluginBundle.getMessage
import pl.dwojciechowski.model.ActionPresentationOption
import pl.dwojciechowski.ui.PLMPluginNotification
import pl.dwojciechowski.ui.component.RemotePickerButton
import java.awt.event.ActionEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*

class PluginSettingsDialog(private val project: Project) : DialogWrapper(project), Disposable {

    private val config: PluginConfiguration = ServiceManager.getService(project, PluginConfiguration::class.java)

    lateinit var content: JPanel

    // URL Settings
    private lateinit var protocolCB: JComboBox<String>
    private lateinit var hostnameField: JTextField
    private lateinit var portSpinner: JSpinner
    private lateinit var windchillRelativeTextField: JTextField
    private lateinit var resultingTextField: JTextField

    // Remote system settings
    private lateinit var loginField: JTextField
    private lateinit var passwordField: JPasswordField
    private lateinit var logFileLocation: JTextField
    private lateinit var remotePickerButton: JButton
    private lateinit var folderPickerButton: JButton
    private lateinit var folderPathTextFile: JTextField

    // Plugin Settings
    private lateinit var refreshRateSpinner: JSpinner
    private lateinit var timeoutSpinner: JSpinner
    private lateinit var addonPortSpinner: JSpinner
    private lateinit var actionPresentationCB: JComboBox<String>
    private lateinit var statusControlled: JCheckBox
    private lateinit var autoOpenCommandPane: JCheckBox

    fun createUIComponents() {
        actionPresentationCB = ComboBox(ActionPresentationOption.ALL_OPTIONS.toArray(arrayOf()))

        val conf = ServiceManager.getService(project, PluginConfiguration::class.java)

        remotePickerButton = RemotePickerButton(project, conf.logFileLocation) {
            logFileLocation.text = it.first()
            config.logFileLocation = logFileLocation.text
        }
        folderPickerButton = RemotePickerButton(project, conf.lffFolder) {
            folderPathTextFile.text = it.first()
            config.lffFolder = folderPathTextFile.text
        }

    }

    init {
        title = getMessage("ui.config.title")
        //spinner init
        refreshRateSpinner.model = SpinnerNumberModel(1000, 500, Int.MAX_VALUE, 100)
        refreshRateSpinner.editor = JSpinner.NumberEditor(refreshRateSpinner, "# ms")
        timeoutSpinner.model = SpinnerNumberModel(5000, 500, Int.MAX_VALUE, 100)
        timeoutSpinner.editor = JSpinner.NumberEditor(timeoutSpinner, "# ms")
        portSpinner.model = SpinnerNumberModel(8080, 1, 9999, 1)
        portSpinner.editor = JSpinner.NumberEditor(portSpinner, ":#")
        addonPortSpinner.model = SpinnerNumberModel(4040, 1, 9999, 1)
        addonPortSpinner.editor = JSpinner.NumberEditor(addonPortSpinner, "#")

        protocolCB.addActionListener { resultingTextField.composeURL() }
        portSpinner.addChangeListener { resultingTextField.composeURL() }
        hostnameField.addOnKeyEvent { resultingTextField.composeURL() }
        windchillRelativeTextField.addOnKeyEvent { resultingTextField.composeURL() }

        actionPresentationCB.addActionListener {
            config.actionPresentation = actionPresentationCB.selectedItem as String
        }

        initFromConfig()
        init()
    }

    private fun JTextField.addOnKeyEvent(method: () -> Unit) {
        this.addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent?) {
                super.keyReleased(e)
                method()
            }
        })
    }

    override fun createCenterPanel() = content
    override fun dispose() = super.dispose()

    private fun initFromConfig() {
        protocolCB.selectedItem = config.protocol
        hostnameField.text = config.hostname
        windchillRelativeTextField.text = config.relativePath
        portSpinner.value = config.port

        loginField.text = config.login
        passwordField.text = config.passwd
        refreshRateSpinner.value = config.refreshRate
        timeoutSpinner.value = config.timeout
        logFileLocation.text = config.logFileLocation
        addonPortSpinner.value = config.addonPort
        actionPresentationCB.selectedItem = config.actionPresentation
        folderPathTextFile.text = config.lffFolder
        statusControlled.isSelected = config.statusControlled
        autoOpenCommandPane.isSelected = config.autoOpenCommandPane

        resultingTextField.composeURL()
    }

    private fun saveConfig() {
        config.protocol = protocolCB.selectedItem as String
        config.hostname = hostnameField.text
        config.relativePath = windchillRelativeTextField.text
        config.port = portSpinner.value as Int

        config.login = loginField.text
        config.passwd = String(passwordField.password)
        config.refreshRate = refreshRateSpinner.value as Int
        config.timeout = timeoutSpinner.value as Int
        config.logFileLocation = logFileLocation.text
        config.addonPort = addonPortSpinner.value as Int
        config.actionPresentation = actionPresentationCB.selectedItem as String
        config.lffFolder = folderPathTextFile.text
        config.statusControlled = statusControlled.isSelected
        config.autoOpenCommandPane = autoOpenCommandPane.isSelected
    }

    private fun JTextField.composeURL() {
        this.text = "${protocolCB.selectedItem as String}://${hostnameField.text}:${portSpinner.value as Int}" +
                windchillRelativeTextField.text
    }

    override fun getOKAction(): Action =
        object : AbstractAction("OK") {
            override fun actionPerformed(e: ActionEvent?) {
                saveConfig()
                PLMPluginNotification.settingsSaved(project)
                dispose()
            }
        }

    override fun getCancelAction(): Action =
        object : AbstractAction("Cancel") {
            override fun actionPerformed(e: ActionEvent?) {
                dispose()
            }
        }

}