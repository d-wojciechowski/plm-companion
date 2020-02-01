package pl.dwojciechowski.ui.component

import com.intellij.icons.AllIcons
import pl.dwojciechowski.model.CommandBean
import java.awt.Component
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

        val nodeVal = value as CommandBean

        icon = if (nodeVal.name.isNotEmpty()) AllIcons.Nodes.ObjectTypeAttribute else AllIcons.Xml.Css_class

        return this
    }
}