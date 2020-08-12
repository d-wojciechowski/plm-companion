package pl.dwojciechowski.configuration

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "PLMCompanionGlobalConfiguration", storages = [Storage(value = "PLMCompanionGlobalConfiguration.xml")])
class GlobalPluginConfiguration : PersistentStateComponent<GlobalPluginConfiguration> {

    var installedVersion: String = ""

    override fun getState() = this

    override fun loadState(configuration: GlobalPluginConfiguration) {
        XmlSerializerUtil.copyBean(configuration, this)
    }

}