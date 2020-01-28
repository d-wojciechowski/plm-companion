package pl.dwojciechowski.ui.component

import com.intellij.openapi.ui.JBMenuItem
import com.intellij.openapi.ui.JBPopupMenu
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBTextField
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseEvent
import javax.swing.AbstractAction
import javax.swing.DefaultListModel
import javax.swing.JPopupMenu
import javax.swing.UIManager
import javax.swing.border.EmptyBorder


data class CommandListRMBMenu(
    private val invoker: JBList<CommandRepresenation>
) {
    private val menu = JBPopupMenu()

    init {
        val editItem = JBMenuItem("Edit")
        editItem.addActionListener(EditListAction(invoker))
        menu.add(editItem)

        val deleteItem = JBMenuItem("Delete")
        deleteItem.addActionListener { println("Delete!") }
        menu.add(deleteItem)

        val setNameItem = JBMenuItem("Alias")
        setNameItem.addActionListener { println("Alias!") }
        menu.add(setNameItem)

    }

    fun show(e: MouseEvent?) {
        menu.show(invoker, e?.x ?: 0, e?.y ?: 0)
    }

    fun addItem(itemName: String, index: Int = -1, action: (ActionEvent) -> Unit) : CommandListRMBMenu{
        val item = JBMenuItem(itemName)
        item.addActionListener(action)
        if(index == -1){
            menu.add(item)
        } else {
            menu.add(item, index)
        }
        return this
    }

    class EditListAction(
        private val list: JBList<CommandRepresenation>
    ) : AbstractAction() {
        private lateinit var editPopup: JPopupMenu
        private lateinit var editTextField: JBTextField


        override fun actionPerformed(e: ActionEvent) {
            if(!this::editPopup.isInitialized){
                createEditPopup()
            }
            val r = list.getCellBounds(list.selectedIndex, list.selectedIndex)
            editPopup.preferredSize = Dimension(r.width, r.height)
            editPopup.show(list, r.x, r.y)
            //  Prepare the text field for editing
            editTextField.text = list.selectedValue.toString()
            editTextField.selectAll()
            editTextField.requestFocusInWindow()
            editTextField.grabFocus()
            editTextField.requestFocus()
            editTextField.hasFocus()
        }

        private fun createEditPopup() {
            editTextField = JBTextField()
            editTextField.border = UIManager.getBorder("List.focusCellHighlightBorder")

            editTextField.addActionListener (object: ActionListener{
                override fun actionPerformed(e: ActionEvent?) {
                    println("AM I WORKING?")
                    val value = editTextField.text
                    val model = list.model as DefaultListModel<CommandRepresenation>
                    val row = list.selectedIndex
                    model.get(row).command = value
                    editPopup.isVisible = false
                }

            })

            //  Add the editor to the popup
            editPopup = JPopupMenu()
            editPopup.border = EmptyBorder(0, 0, 0, 0)
            editPopup.add(editTextField)
        }
    }

}