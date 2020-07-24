package pl.dwojciechowski.model

import com.intellij.icons.AllIcons
import jdk.nashorn.internal.runtime.ECMAErrors.getMessage
import pl.dwojciechowski.ui.PluginIcons
import javax.swing.Icon

enum class ExecutionStatus(val icon: Icon, val format: String) {

    RUNNING(PluginIcons.RUNNING, getMessage("execution.status.running")),
    STOPPED(PluginIcons.ERROR, getMessage("execution.status.stopped")),
    COMPLETED(PluginIcons.CONFIRMATION, "execution.status.completed"),
    NONE(AllIcons.Debugger.Db_muted_breakpoint, "execution.status.none");

    fun getMessage(command: CommandBean) = format.replace("%s", command.toString())

}