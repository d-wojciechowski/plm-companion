package pl.dwojciechowski.ui.component

import com.intellij.ui.components.JBList
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.ListModel
import javax.swing.SwingUtilities

class CommandList : JBList<CommandRepresenation> {

    constructor() : super()
    constructor(listModel: ListModel<CommandRepresenation>) : super(listModel)

    private var hasRightClickMenu = false
    private var popupMenu = CommandListRMBMenu(this)

    init {
        cellRenderer = CommandListCellRenderer()

        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent?) {
                selectedIndex = locationToIndex(e?.point)
                if (hasRightClickMenu && SwingUtilities.isRightMouseButton(e)) {
                    popupMenu.show(e)
                }
            }
        })
    }

    fun addRMBMenuEntry(itemName: String, index: Int = -1, action: (ActionEvent) -> Unit): CommandList {
        hasRightClickMenu = true
        popupMenu.addItem(itemName, index, action)
        return this
    }

    fun addRMBMenuEntry(itemName: String, index: Int = -1, action: ActionListener): CommandList {
        hasRightClickMenu = true
        popupMenu.addItem(itemName, index, action)
        return this
    }

}