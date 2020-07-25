package pl.dwojciechowski.ui.component

import com.intellij.execution.RunManager
import com.intellij.execution.RunManagerEx
import com.intellij.icons.AllIcons
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.ui.DeferredIcon
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.i18n.PluginBundle.getMessage
import java.awt.Component
import javax.swing.DefaultComboBoxModel
import javax.swing.DefaultListCellRenderer
import javax.swing.JComboBox
import javax.swing.JList

class RunConfigurationComboBox(
    private val project: Project,
    withEmptyElement: Boolean = true
) : JComboBox<RunConfigurationRepresentation>() {

    private val config = ServiceManager.getService(project, PluginConfiguration::class.java)
    private val cbModel = model as DefaultComboBoxModel<RunConfigurationRepresentation>

    init {
        renderer = RunConfigurationCBRenderer()

        if (withEmptyElement) {
            cbModel.addElement(RunConfigurationRepresentation("", getMessage("runconfig.donothing.displayname"), AllIcons.Plugins.Disabled))
        }

        val allSettings = RunManager.getInstance(project).allSettings
        allSettings.forEach {
            val icon = RunManagerEx.getInstanceEx(project).getConfigurationIcon(it, false) as DeferredIcon
            val runConfigurationRepresentation = RunConfigurationRepresentation(it.uniqueID, it.name, icon, it)
            cbModel.addElement(runConfigurationRepresentation)
            if (it.uniqueID == config.lfPreRunUniqueID) cbModel.selectedItem = runConfigurationRepresentation
        }
        addActionListener {
            config.lfPreRunUniqueID = (cbModel.selectedItem as RunConfigurationRepresentation).uniqueId
        }
    }


    private class RunConfigurationCBRenderer : DefaultListCellRenderer() {

        override fun getListCellRendererComponent(
            list: JList<*>?, value: Any,
            index: Int, isSelected: Boolean, cellHasFocus: Boolean
        ): Component? {
            super.getListCellRendererComponent(
                list, value, index, isSelected,
                cellHasFocus
            )
            val runnerRep = value as RunConfigurationRepresentation
            icon = runnerRep.icon
            return this
        }

    }

    fun getSelectedConfiguration(): RunConfigurationRepresentation {
        return cbModel.selectedItem as RunConfigurationRepresentation
    }

}