package pl.dominikw.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.Messages
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.dominikw.model.ServerStatus
import pl.dominikw.service.HttpService
import javax.swing.*

internal class WindchillWindowPanel : Disposable {

    var content: JPanel? = null
    private var restartWindchillButton: JButton? = null
    private var windchillStatusLabel: JLabel? = null
    private var serverUrlTextField: JTextField? = null
    private var preserveConfig: JCheckBox? = null

    init {
        restartWindchillButton?.addActionListener { restartWindchill() }
        preserveConfig?.addActionListener { disableConfig() }
        windchillStatusLabel?.text = "ON"
        windchillStatusLabel?.icon = PluginIcons.OK

        serverUrlTextField?.text = "INIT"

        GlobalScope.launch {
            while (true) {
                if (serverUrlTextField?.text != "INIT") {
                    windchillStatusLabel?.icon = when (HttpService.getInstance().getStatus(serverUrlTextField!!.text)) {
                        ServerStatus.DOWN -> PluginIcons.KO
                        ServerStatus.RUNNING -> PluginIcons.OK
                        ServerStatus.STARTING -> PluginIcons.LOAD
                    }
                }
                delay(1000)
            }
        }
    }

    private fun restartWindchill() {
        Messages.showMessageDialog("Hello world!", "Greeting", Messages.getInformationIcon())
    }

    private fun disableConfig() {
        serverUrlTextField?.isEnabled = !serverUrlTextField?.isEnabled!!
        restartWindchillButton?.isEnabled = !restartWindchillButton?.isEnabled!!
    }

    override fun dispose() {

    }

}
