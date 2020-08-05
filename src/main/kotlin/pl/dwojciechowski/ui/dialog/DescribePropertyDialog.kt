package pl.dwojciechowski.ui.dialog

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import pl.dwojciechowski.i18n.PluginBundle
import javax.swing.JPanel

class DescribePropertyDialog(private val project: Project) : DialogWrapper(project), Disposable {

    lateinit var content: JPanel

    init {
        init()
        title = PluginBundle.getMessage("ui.dpd.title")
    }

    override fun createCenterPanel() = content
    override fun dispose() = super.dispose()


}