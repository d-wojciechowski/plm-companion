package pl.dwojciechowski.model

import pl.dwojciechowski.configuration.PluginConfiguration

data class HttpStatusConfig(
    val url: String,
    val login: String,
    val password: String,
    val timeout: Int
) {

    constructor(config: PluginConfiguration, onlyBase: Boolean = false) : this(
        url = "${config.protocol}://${config.hostname}:${config.port}${if (!onlyBase) config.relativePath else ""}",
        login = config.login,
        password = config.password,
        timeout = config.timeout
    )


}