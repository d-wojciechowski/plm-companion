package pl.dominikw.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.Messages
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bouncycastle.cms.RecipientId.password
import pl.dominikw.model.ServerStatus
import pl.dominikw.service.HttpService
import javax.swing.*

internal class WindchillWindowPanel : Disposable {

    lateinit var content: JPanel
    private lateinit var restartWindchillButton: JButton
    private lateinit var windchillStatusLabel: JLabel
    private lateinit var serverUrlTextField: JTextField
    private lateinit var preserveConfig: JCheckBox

    private lateinit var loginField: JTextField
    private lateinit var passwordField: JPasswordField


    init {
        restartWindchillButton.addActionListener { restartWindchill() }
        preserveConfig.addActionListener { disableConfig() }

        GlobalScope.launch {
            while (true) {
                val url = serverUrlTextField.text
                val login = loginField.text
                val password = String(passwordField.password)
                windchillStatusLabel.set(HttpService.getInstance().getStatus(url, login, password))
                delay(1000)
            }
        }
    }

    private fun restartWindchill() {
        Messages.showMessageDialog("Hello world!", "Greeting", Messages.getInformationIcon())
    }

    private fun disableConfig() {
        serverUrlTextField.isEnabled = !serverUrlTextField.isEnabled
        restartWindchillButton.isEnabled = !restartWindchillButton.isEnabled
    }

    override fun dispose() {

    }

    fun JLabel.set(status: ServerStatus) {
        this.icon = status.icon
        this.text = status.label
    }

}
