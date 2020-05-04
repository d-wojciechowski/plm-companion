package pl.dwojciechowski.ui.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.ex.CustomComponentAction
import com.intellij.openapi.actionSystem.ex.CustomComponentAction.COMPONENT_KEY
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBUI
import io.reactivex.rxjava3.disposables.Disposable
import pl.dwojciechowski.service.StatusService
import pl.dwojciechowski.ui.PluginIcons
import javax.swing.JComponent

class PLMLabelAction : DumbAwareAction(), CustomComponentAction {

    lateinit var subscription: Disposable
    var project: Project? = null

    override fun update(e: AnActionEvent) {
        e.presentation.text = "PLM: "
        e.presentation.icon = PluginIcons.ERROR
        if (project != e.project && e.project != null) {
            val clientProperty = e.presentation.getClientProperty(COMPONENT_KEY)
            project = e.project
            val statusService = ServiceManager.getService(project!!, StatusService::class.java)
            if (this::subscription.isInitialized) subscription.dispose()
            subscription = statusService.getOutputSubject().subscribe {
                if(clientProperty is MyLabel){
                    clientProperty.icon = it.icon
                }
                println(" STATUS UPDATED ${it.label}")
            }
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        //do nothing
    }

    override fun createCustomComponent(
        presentation: Presentation,
        place: String
    ): JComponent {
        return MyLabel(presentation)
            .withFont(JBUI.Fonts.toolbarFont())
            .withBorder(JBUI.Borders.empty(0, 6, 0, 5))
    }


    class MyLabel(private val myPresentation: Presentation) :
        JBLabel() {
        private fun updatePresentation() {
            text = StringUtil.notNullize(myPresentation.text)
            toolTipText = StringUtil.nullize(myPresentation.description)
            icon = myPresentation.icon
        }

        init {
            myPresentation.addPropertyChangeListener { e ->
                val propertyName = e.propertyName
                if (Presentation.PROP_TEXT == propertyName || Presentation.PROP_DESCRIPTION == propertyName || Presentation.PROP_ICON == propertyName) {
                    updatePresentation()
                }
            }
            updatePresentation()
        }
    }
}
