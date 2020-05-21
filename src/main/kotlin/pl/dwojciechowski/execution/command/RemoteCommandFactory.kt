package pl.dwojciechowski.execution.command

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.openapi.project.Project


class RemoteCommandFactory(configurationType: ConfigurationType) : ConfigurationFactory(configurationType) {

    override fun createTemplateConfiguration(project: Project) =
        RemoteCommandRunConfig(project, this, "")

    override fun getId() = "PLM_COMPANION.REMOTE_COMMAND"

}