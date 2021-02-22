package pl.dwojciechowski.execution.copy

data class CopyFilesSettings(
    var command: String = "",
    var async: Boolean = true
) : Cloneable {

    companion object {
        const val TAG = "CopyFilesSettings"
    }

}