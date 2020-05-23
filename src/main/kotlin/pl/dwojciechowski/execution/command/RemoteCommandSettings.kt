package pl.dwojciechowski.execution.command

data class RemoteCommandSettings(
    var command: String = "",
    var async: Boolean = true
) : Cloneable {

    companion object {
        const val TAG = "RemoteCommandSettings"
    }

}