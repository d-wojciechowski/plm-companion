package pl.dwojciechowski.ui.component

import com.intellij.icons.AllIcons
import java.awt.Component
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer

class FileExplorerCellTreeRenderer : DefaultTreeCellRenderer() {

    override fun getTreeCellRendererComponent(
        tree: JTree, value: Any,
        sel: Boolean, exp: Boolean, leaf: Boolean, row: Int, hasFocus: Boolean
    ): Component {
        super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus)

        val node = value as DefaultMutableTreeNode
        val nodeVal = node.userObject as RemoteFileRepresentation
        this.openIcon = AllIcons.Actions.Menu_open
        this.closedIcon = AllIcons.Nodes.Folder
        this.leafIcon = AllIcons.Nodes.UnknownJdk

        if(!nodeVal.isDirectory) icon = AllIcons.FileTypes.Any_type
        if (nodeVal.empty) icon = AllIcons.Actions.GroupByPackage

        return this
    }

}