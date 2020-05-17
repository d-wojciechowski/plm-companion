package pl.dwojciechowski.ui.panel

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import pl.dwojciechowski.service.NotificationService
import javax.swing.JPanel

internal class PLMCompanionPanel(private val project: Project) {

    lateinit var content: JPanel
    private lateinit var customCommandPanel: JPanel
    private lateinit var fatButtonPanel: JPanel

    fun createUIComponents() {
        customCommandPanel = CommandSubPanel(project).content
        fatButtonPanel = FatButtonPanel(project).content
    }

    init {
        //To init background job
        ServiceManager.getService(project, NotificationService::class.java)
    }

}
