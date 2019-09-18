package pl.dominikw.configuration

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.util.xmlb.XmlSerializerUtil


@State(name = "WindchillPluginConfiguration", storages = [Storage(value = StoragePathMacros.WORKSPACE_FILE)])
class PluginConfiguration() : PersistentStateComponent<PluginConfiguration> {

    var login: String = ""
    var password: String = ""
    var hostname: String = ""
    var protocol: String = ""

    var preserveConfig: Boolean = false
    var scanWindchill: Boolean = false

    var port: Int = 80
    var refreshRate: Int = 1000

    override fun getState() = this

    override fun loadState(config: PluginConfiguration) {
        XmlSerializerUtil.copyBean(config, this)
    }

}