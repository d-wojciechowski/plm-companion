package pl.dwojciechowski.ui.dialog

import com.intellij.CommonBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBLabel
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.swing.JPanel

class WelcomeDialog(
    private val project: Project
) : DialogWrapper(project) {

    private lateinit var rootPane: JPanel
    private lateinit var editorPane: JBLabel

    init {
        init()
        setCancelButtonText(CommonBundle.getCloseButtonText())

        title = "Remote File Picker Dialog"
        editorPane.text = loadMessageText()
    }

    override fun createActions() = arrayOf(cancelAction)

    override fun createCenterPanel() = rootPane

    private fun loadMessageText(): String {
        javaClass.classLoader.getResource("META-INF/exa.png")
        return javaClass.classLoader.getResourceAsStream("html/WelcomeScreen.html")?.use { stream ->
            return BufferedReader(InputStreamReader(stream)).readText()
        } ?: ""
    }

}