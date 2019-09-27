package pl.dwojciechowski.model

import pl.dwojciechowski.configuration.PluginConfiguration

data class CommandConfig(
    val hostname: String,
    val timeout: Int
) {

    constructor(config: PluginConfiguration) : this(config.hostname, config.timeout)

}