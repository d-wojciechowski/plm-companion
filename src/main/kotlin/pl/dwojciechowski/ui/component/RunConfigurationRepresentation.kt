package pl.dwojciechowski.ui.component

import com.intellij.execution.RunnerAndConfigurationSettings
import javax.swing.Icon

data class RunConfigurationRepresentation (
    val uniqueId: String,
    val displayName: String,
    val icon: Icon,
    val value: RunnerAndConfigurationSettings? = null
) {

    override fun toString() = displayName

}