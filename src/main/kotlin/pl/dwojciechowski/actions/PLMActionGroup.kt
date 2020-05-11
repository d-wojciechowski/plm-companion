package pl.dwojciechowski.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.components.ServiceManager
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.model.ActionPresentationOption


class PLMActionGroup : DefaultActionGroup() {

    override fun update(event: AnActionEvent) {
        val project = event.project ?: return
        val config = ServiceManager.getService(project, PluginConfiguration::class.java)
        when (config.actionPresentation) {
            ActionPresentationOption.NAVIGATION_AND_PANE -> event.presentation.isVisible = true
            else -> event.presentation.isVisible = false
        }
    }

}