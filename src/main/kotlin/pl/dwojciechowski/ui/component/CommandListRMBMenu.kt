package pl.dwojciechowski.ui.component

import com.intellij.openapi.ui.JBMenuItem
import com.intellij.openapi.ui.JBPopupMenu
import com.intellij.ui.components.JBList
import pl.dwojciechowski.ui.component.action.EditListAction
import java.awt.event.ActionEvent
import java.awt.event.MouseEvent
import javax.swing.DefaultListModel


data class CommandListRMBMenu(
    private val invoker: JBList<CommandRepresenation>
) {
    private val menu = JBPopupMenu()

    init {
        val editItem = JBMenuItem("Edit")
        editItem.addActionListener(EditListAction(invoker))
        menu.add(editItem)

        val deleteItem = JBMenuItem("Delete")
        deleteItem.addActionListener {
            val model = invoker.model as DefaultListModel
            model.remove(invoker.selectedIndex)
        }
        menu.add(deleteItem)

        val setNameItem = JBMenuItem("Alias")
        setNameItem.addActionListener(EditListAction(invoker, "name"))
        menu.add(setNameItem)

    }

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

}