package pl.dwojciechowski.model

import pl.dwojciechowski.ui.PluginIcons
import javax.swing.Icon

enum class ServerStatus(
    val icon: Icon,
    val label: String
) {

    RUNNING(PluginIcons.OK, "Running"),
    DOWN(PluginIcons.KO, "Turned Off"),
    STARTING(PluginIcons.LOAD, "Starting"),
    NOT_SCANNING(PluginIcons.KO, "Scanning Stopped");

}