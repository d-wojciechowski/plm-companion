package pl.dwojciechowski.ui.component

data class RemoteFileRepresentaton(
    val name: String,
    val isDirectory: Boolean
) {
    override fun toString(): String {
        return name;
    }
}