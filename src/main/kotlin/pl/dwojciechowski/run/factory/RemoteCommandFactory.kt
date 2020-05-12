package pl.dwojciechowski.run.factory

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.openapi.project.Project
import pl.dwojciechowski.run.config.RemoteCommandConfigurationBase


class RemoteCommandFactory(configurationType: ConfigurationType) : ConfigurationFactory(configurationType) {

    override fun createTemplateConfiguration(project: Project) =
        RemoteCommandConfigurationBase(project, this, "")

    override fun getId() = "PLM_COMPANION.REMOTE_COMMAND"

}