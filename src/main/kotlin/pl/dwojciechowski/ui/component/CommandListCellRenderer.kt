package pl.dwojciechowski.ui.component

import com.intellij.icons.AllIcons
import pl.dwojciechowski.model.CommandBean
import java.awt.Component
import java.time.format.DateTimeFormatter
import javax.swing.DefaultListCellRenderer
import javax.swing.JList

class CommandListCellRenderer : DefaultListCellRenderer() {

    override fun getListCellRendererComponent(
        list: JList<*>?,
        value: Any?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
        val command = value as CommandBean

        if(command.status != CommandBean.ExecutionStatus.NONE){
            text = "$command | ${command.executionTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))}"
        }
        icon = when(command.status){
            CommandBean.ExecutionStatus.COMPLETED -> AllIcons.RunConfigurations.ToolbarPassed
            CommandBean.ExecutionStatus.RUNNING ->  AllIcons.RunConfigurations.TestState.Run
            CommandBean.ExecutionStatus.STOPPED ->  AllIcons.Debugger.KillProcess
            else -> if (command.name.isNotEmpty()) AllIcons.Nodes.ObjectTypeAttribute else AllIcons.Xml.Css_class
        }

        return this
    }
}