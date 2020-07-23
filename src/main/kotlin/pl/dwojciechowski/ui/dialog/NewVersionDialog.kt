package pl.dwojciechowski.ui.dialog

import com.intellij.CommonBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBLabel
import pl.dwojciechowski.ui.PluginIcons
import java.awt.Font
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.swing.JPanel
import javax.swing.SwingConstants

class NewVersionDialog(project: Project) : DialogWrapper(project) {

    lateinit var htmlContent: JBLabel
    lateinit var icon: JBLabel
    lateinit var centerPanel: JPanel

    override fun createCenterPanel() = centerPanel

    fun createUIComponents() {
        htmlContent = JBLabel()
            .setCopyable(true)
            .setAllowAutoWrapping(true)
        icon = JBLabel("PLM Companion", PluginIcons.scaleToSize(PluginIcons.PLUGIN_BIG, 60), SwingConstants.CENTER)
    }

    init {
        init()
        htmlContent.text = loadMessageText()
        isModal = false
        setResizable(false)

        icon.font = Font(icon.name, Font.BOLD, 30)
        setCancelButtonText(CommonBundle.getCloseButtonText())
    }

    override fun createActions() = arrayOf(cancelAction)

    private fun loadMessageText(): String {
        return javaClass.classLoader.getResourceAsStream("html/NewVersionDialogContent.html")?.use { stream ->
            return BufferedReader(InputStreamReader(stream)).readText()
        } ?: ""
    }

}