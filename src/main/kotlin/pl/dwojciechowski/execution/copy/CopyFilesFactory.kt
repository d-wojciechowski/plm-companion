package pl.dwojciechowski.execution.copy

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.openapi.project.Project

class CopyFilesFactory(configurationType: ConfigurationType) : ConfigurationFactory(configurationType) {

    override fun createTemplateConfiguration(project: Project) =
        CopyFilesRunConfig(project, this, "")

    override fun getId() = "PLMCompanion.CopyFiles"

}