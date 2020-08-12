package pl.dwojciechowski.configuration

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.util.xmlb.XmlSerializerUtil
import pl.dwojciechowski.model.ActionPresentationOption

@State(name = "PLMCompanionConfiguration", storages = [Storage(value = StoragePathMacros.WORKSPACE_FILE)])
class PluginConfiguration : PersistentStateComponent<PluginConfiguration> {

    var installedVersion: String = "" //TODO make it global

    var login: String = ""
    var passwd: String = ""

    var hostname: String = ""
    var relativePath: String = ""
    var protocol: String = ""
    var logFileLocation: String = ""
    var actionPresentation: String = ActionPresentationOption.NAVIGATION_AND_PANE

    var scanWindchill: Boolean = true
    var statusControlled: Boolean = true
    var wrapLogPane: Boolean = true
    var wrapCommandPane: Boolean = false
    var commandAutoScroll: Boolean = true
    var logPanelAutoScroll: Boolean = true
    var autoOpenCommandPane: Boolean = false

    var port: Int = 80
    var addonPort: Int = 4040
    var refreshRate: Int = 1000
    var timeout: Int = 5000

    var commandsHistory = mutableListOf<String>()
    var propertiesHistory = mutableListOf<String>()

    // Load From file
    var lffFolder: String = ""
    var lffTarget: Int = 0
    var lffOrgName: String = ""
    var lffContName: String = ""
    var lfPreRunUniqueID: String = ""

    override fun getState() = this

    override fun loadState(config: PluginConfiguration) {
        XmlSerializerUtil.copyBean(config, this)
    }

}