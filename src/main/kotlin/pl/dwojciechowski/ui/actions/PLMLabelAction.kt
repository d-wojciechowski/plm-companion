package pl.dwojciechowski.ui.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.ex.CustomComponentAction
import com.intellij.openapi.actionSystem.ex.CustomComponentAction.COMPONENT_KEY
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBUI
import pl.dwojciechowski.ui.actions.utils.ActionSubscription
import javax.swing.JComponent

class PLMLabelAction : DumbAwareAction(), CustomComponentAction {

    private val actionSubscription = ActionSubscription()

    override fun update(e: AnActionEvent) {
        actionSubscription.subscriptionRoutine(e) { it, _ ->
            val clientProperty = e.presentation.getClientProperty(COMPONENT_KEY)
            if (clientProperty is MyLabel) {
                clientProperty.icon = it.icon
            }
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        // Just a label, so no action is performed on click
    }

    override fun createCustomComponent(
        presentation: Presentation,
        place: String
    ): JComponent {
        presentation.text = "PLM: "
        return MyLabel(presentation)
            .withFont(JBUI.Fonts.toolbarFont())
            .withBorder(JBUI.Borders.empty(0, 6, 0, 5))
    }


    class MyLabel(private val myPresentation: Presentation) : JBLabel() {

        private fun updatePresentation() {
            text = StringUtil.notNullize(myPresentation.text)
            toolTipText = StringUtil.nullize(myPresentation.description)
            icon = myPresentation.icon
        }

        init {
            myPresentation.addPropertyChangeListener { e ->
                val propertyName = e.propertyName
                if (Presentation.PROP_TEXT == propertyName
                    || Presentation.PROP_DESCRIPTION == propertyName
                    || Presentation.PROP_ICON == propertyName
                ) {
                    updatePresentation()
                }
            }
            updatePresentation()
        }

    }

}
