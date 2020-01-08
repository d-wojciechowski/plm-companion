package pl.dwojciechowski.model

import com.intellij.icons.AllIcons
import pl.dwojciechowski.ui.PluginIcons
import javax.swing.Icon

enum class ServerStatus(
    val icon: Icon,
    val label: String
) {

    RUNNING(PluginIcons.RUNNING, "Running"),
    DOWN(PluginIcons.ERROR, "Turned Off"),
    REACHABLE(PluginIcons.WARNING, "Server reachable"),
    NOT_SCANNING(AllIcons.Debugger.Db_field_breakpoint, "Scanning Stopped"),
    UNAUTHORIZED(AllIcons.Ide.Readonly, "Unauthorized");

}
