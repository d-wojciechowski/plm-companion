package pl.dwojciechowski.ui.component

data class RemoteFileRepresentation(
    val name: String,
    val isDirectory: Boolean,
    var empty: Boolean = false
) {
    override fun toString(): String {
        return name
    }
}