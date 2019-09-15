package pl.dominikw.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.Messages
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.dominikw.service.HttpService
import pl.dominikw.model.ServerStatus
import javax.swing.*

internal class WindchillWindowPanel : Disposable {

    lateinit var content: JPanel
    private lateinit var restartWindchillButton: JButton
    private lateinit var windchillStatusLabel: JLabel
    private lateinit var serverUrlTextField: JTextField
    private lateinit var preserveConfig: JCheckBox

    init {
        restartWindchillButton.addActionListener { restartWindchill() }
        preserveConfig.addActionListener { disableConfig() }

        GlobalScope.launch {
            while (true) {
                windchillStatusLabel.set(HttpService.getInstance().getStatus(serverUrlTextField.text))
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
