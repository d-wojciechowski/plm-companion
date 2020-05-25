package pl.dwojciechowski.model

import com.intellij.icons.AllIcons
import pl.dwojciechowski.ui.PluginIcons
import javax.swing.Icon

enum class ExecutionStatus(val icon: Icon, val format: String) {
    RUNNING(PluginIcons.RUNNING, "Command %s started."),
    STOPPED(PluginIcons.ERROR, "Command %s failed."),
    COMPLETED(PluginIcons.CONFIRMATION, "Command %s finished successfully"),
    NONE(AllIcons.Debugger.Db_muted_breakpoint, "Command %s created");

    fun getMessage(command: CommandBean) = format.replace("%s", command.toString())
}