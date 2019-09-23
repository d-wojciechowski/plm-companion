package pl.dwojciechowski.ui.dialog

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.JPanel

class LoadFromFileDialog(
    private val project: Project
) : DialogWrapper(project) {

    private lateinit var rootPane: JPanel

    init {
        init()
    }

    override fun createCenterPanel() = rootPane

}