package pl.dwojciechowski.ui.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.wm.impl.ToolWindowImpl
import javax.swing.Icon

class ToggleLoggingAction(
    private val toolWindow: ToolWindowImpl,
    private val onActionIcon: Icon = AllIcons.Actions.Checked,
    private val offActionIcon: Icon = AllIcons.Actions.Cancel,
    initState: Boolean = false,
    private val action: (e: AnActionEvent) -> Unit
) {

    private val onAction = object :
        DumbAwareAction("Log enabled", "Disable Logging", onActionIcon) {

        lateinit var otherAction: AnAction

        override fun actionPerformed(e: AnActionEvent) {
            action.invoke(e)
            toolWindow.setTabActions(otherAction)
        }
    }

    private val offAction = object :
        DumbAwareAction("Log disabled", "Enable Logging", offActionIcon) {

        lateinit var otherAction: AnAction

        override fun actionPerformed(e: AnActionEvent) {
            action.invoke(e)
            toolWindow.setTabActions(otherAction)
        }
    }

    init {
        onAction.otherAction = offAction
        offAction.otherAction = onAction

        toolWindow.setTabActions(if (initState) onAction else offAction)
    }

}