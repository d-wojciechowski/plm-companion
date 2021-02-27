package pl.dwojciechowski.ui.component

import com.intellij.ui.components.JBList
import pl.dwojciechowski.execution.copy.FolderConfig
import java.awt.event.*
import javax.swing.ListModel
import javax.swing.SwingUtilities

class CopyFileConfigList : JBList<FolderConfig> {

    constructor() : super()
    constructor(listModel: ListModel<FolderConfig>) : super(listModel)

    private var hasRightClickMenu = false
//    private var popupMenu = CommandListRMBMenu(this)

    init {
        cellRenderer = CommandListCellRenderer()

        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent?) {
                selectedIndex = locationToIndex(e?.point)
                if (hasRightClickMenu && SwingUtilities.isRightMouseButton(e)) {
//                    popupMenu.show(e)
                }
            }
        })
    }

    fun addKeyPressedListener(action: (KeyEvent?) -> Unit) {
        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent?) {
                action(e)
            }
        })
    }

    fun addMousePressedListener(action: (MouseEvent?) -> Unit) {
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent?) {
                action(e)
            }
        })
    }

    fun addRMBMenuEntry(itemName: String, index: Int = -1, action: (ActionEvent) -> Unit): CopyFileConfigList {
        hasRightClickMenu = true
//        popupMenu.addItem(itemName, index, action)
        return this
    }

    fun addRMBMenuEntry(itemName: String, index: Int = -1, action: ActionListener): CopyFileConfigList {
        hasRightClickMenu = true
//        popupMenu.addItem(itemName, index, action)
        return this
    }

}