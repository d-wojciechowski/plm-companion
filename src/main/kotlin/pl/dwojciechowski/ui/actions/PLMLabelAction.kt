package pl.dwojciechowski.ui.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ex.ToolbarLabelAction

class PLMLabelAction : ToolbarLabelAction() {

    override fun update(e: AnActionEvent) {
        e.presentation.text = "PLM: "
    }

}