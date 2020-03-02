package pl.dwojciechowski.ui.component

import com.intellij.openapi.ui.JBMenuItem
import com.intellij.openapi.ui.JBPopupMenu
import com.intellij.ui.components.JBList
import pl.dwojciechowski.model.CommandBean
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseEvent


data class CommandListRMBMenu(
    private val invoker: JBList<CommandBean>
) {
    private val menu = JBPopupMenu()

    fun show(e: MouseEvent?) {
        menu.show(invoker, e?.x ?: 0, e?.y ?: 0)
    }

    fun addItem(itemName: String, index: Int = -1, action: (ActionEvent) -> Unit): CommandListRMBMenu {
        val item = JBMenuItem(itemName)
        item.addActionListener(action)
        if (index == -1) {
            menu.add(item)
        } else {
            menu.add(item, index)
        }
        return this
    }

    fun addItem(itemName: String, index: Int = -1, action: ActionListener): CommandListRMBMenu {
        val item = JBMenuItem(itemName)
        item.addActionListener(action)
        if (index == -1) {
            menu.add(item)
        } else {
            menu.add(item, index)
        }
        return this
    }

}