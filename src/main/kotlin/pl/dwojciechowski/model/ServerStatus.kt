package pl.dwojciechowski.model

import com.intellij.icons.AllIcons
import pl.dwojciechowski.i18n.PluginBundle.getMessage
import pl.dwojciechowski.ui.PluginIcons
import javax.swing.Icon

enum class ServerStatus(
    val icon: Icon,
    val label: String
) {

    RUNNING(PluginIcons.RUNNING, getMessage("server.status.running")),
    DOWN(PluginIcons.ERROR, getMessage("server.status.down")),
    AVAILABLE(PluginIcons.WARNING, getMessage("server.status.available")),
    NOT_SCANNING(AllIcons.Debugger.Db_field_breakpoint, getMessage("server.status.not_scanning")),
    UNAUTHORIZED(AllIcons.Ide.Readonly, getMessage("server.status.unauthorized"));

}
