package pl.dwojciechowski.ui.panel

import com.intellij.openapi.project.Project
import javax.swing.JPanel
import javax.swing.JTabbedPane
import javax.swing.JTextArea

class LogViewerPanel(private val project: Project){

    lateinit var content: JPanel
    private lateinit var tabPane : JTabbedPane

    private lateinit var ms : JTextArea
    private lateinit var bms : JTextArea

}