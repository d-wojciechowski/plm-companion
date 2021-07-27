package pl.dwojciechowski.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import pl.dwojciechowski.configuration.ProjectPluginConfiguration
import pl.dwojciechowski.model.ActionPresentationOption

class PLMActionGroup : DefaultActionGroup() {

    override fun update(event: AnActionEvent) {
        val project = event.project ?: return
        val config = project.getService(ProjectPluginConfiguration::class.java)
        when (config.actionPresentation) {
            ActionPresentationOption.NAVIGATION_AND_PANE -> event.presentation.isVisible = true
            else -> event.presentation.isVisible = false
        }
    }

}