package pl.dwojciechowski.execution.copy

import java.io.Serializable

data class CopyFilesSettings(
    val folderConfigs: MutableList<FolderConfig> = mutableListOf(),
    var async: Boolean = true
) : Cloneable {

    companion object {
        const val TAG = "CopyFilesSettings"
    }

}

data class FolderConfig(
    var srcDir: String = "",
    var targetDir: String = "",
    var ignoredExtensions: MutableList<String> = mutableListOf(),
) : Cloneable, Serializable