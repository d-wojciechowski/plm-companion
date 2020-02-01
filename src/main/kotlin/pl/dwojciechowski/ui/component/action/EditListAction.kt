package pl.dwojciechowski.ui.component.action

import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBTextField
import pl.dwojciechowski.model.CommandBean
import java.awt.Dimension
import java.awt.event.*
import javax.swing.AbstractAction
import javax.swing.DefaultListModel
import javax.swing.JPopupMenu
import javax.swing.UIManager
import javax.swing.border.EmptyBorder

class EditListAction(
    private val list: JBList<CommandBean>,
    private val commandFieldName: String = "command"
) : AbstractAction() {

    private lateinit var editPopup: JPopupMenu
    private lateinit var editTextField: JBTextField
    private var selectedIndex = -1
    private val model = list.model as DefaultListModel<CommandBean>

    override fun actionPerformed(e: ActionEvent) {
        if (!this::editPopup.isInitialized) {
            createEditPopup()
        }
        val r = list.getCellBounds(list.selectedIndex, list.selectedIndex)
        editPopup.preferredSize = Dimension(r.width, r.height)
        editPopup.show(list, r.x, r.y)

        selectedIndex = list.selectedIndex
        editTextField.text = if (commandFieldName == "command") {
            model.get(selectedIndex).command
        } else {
            model.get(selectedIndex).name
        }
        editTextField.selectAll()
        editTextField.requestFocusInWindow()
    }

    private fun createEditPopup() {
        editTextField = JBTextField()
        editTextField.border = UIManager.getBorder("List.focusCellHighlightBorder")

        editTextField.addKeyListener(object : KeyAdapter() {
            override fun keyTyped(e: KeyEvent?) {
                if (e?.keyChar?.toInt() == KeyEvent.VK_ENTER) {
                    copyValueToListEntry()
                }
            }
        })

        editTextField.addFocusListener(object : FocusAdapter() {
            override fun focusLost(e: FocusEvent?) {
                copyValueToListEntry()
            }
        })

        editPopup = JPopupMenu()
        editPopup.border = EmptyBorder(0, 0, 0, 0)
        editPopup.add(editTextField)
    }

    private fun copyValueToListEntry() {
        val value = editTextField.text
        if (commandFieldName == "command") {
            model.get(selectedIndex).command = value
        } else {
            model.get(selectedIndex).name = value
        }
        editPopup.isVisible = false
    }

}