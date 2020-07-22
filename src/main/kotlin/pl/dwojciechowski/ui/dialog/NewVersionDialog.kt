package pl.dwojciechowski.ui.dialog

import com.intellij.CommonBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBLabel
import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.panel
import com.intellij.util.ui.JBDimension
import pl.dwojciechowski.ui.PluginIcons
import java.awt.Font
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.swing.SwingConstants

class NewVersionDialog(project: Project) : DialogWrapper(project) {

    val htmlContent = JBLabel(loadMessageText())
    val icon = JBLabel("PLM Companion", PluginIcons.scaleToSize(PluginIcons.PLUGIN_BIG, 60), SwingConstants.CENTER)

    override fun createCenterPanel() = panel {
        row(separated = true) {
            icon(CCFlags.grow)
        }
        row {
            htmlContent(CCFlags.growY)
        }
    }

    init {
        init()
        isModal = false
        icon.font = Font(icon.name, Font.BOLD, 30)
        htmlContent.preferredSize = JBDimension(550, 0)
        setCancelButtonText(CommonBundle.getCloseButtonText())
    }

    override fun createActions() = arrayOf(cancelAction)

    private fun loadMessageText(): String {
        return javaClass.classLoader.getResourceAsStream("html/NewVersionDialogContent.html")?.use { stream ->
            return BufferedReader(InputStreamReader(stream)).readText()
        } ?: ""
    }

}