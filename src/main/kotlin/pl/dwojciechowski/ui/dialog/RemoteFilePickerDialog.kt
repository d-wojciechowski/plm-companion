package pl.dwojciechowski.ui.dialog

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.treeStructure.Tree
import pl.dwojciechowski.proto.files.FileMeta
import pl.dwojciechowski.proto.files.FileResponse
import pl.dwojciechowski.service.FileService
import pl.dwojciechowski.ui.component.FileExplorerCellTreeRenderer
import pl.dwojciechowski.ui.component.RemoteFileRepresentation
import java.awt.event.ActionEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.AbstractAction
import javax.swing.Action
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath
import javax.swing.tree.TreeSelectionModel

class RemoteFilePickerDialog(
    project: Project,
    private val startPath: String,
    singleSelect: Boolean = true,
    private val onlyFoldersVisible: Boolean = true
) : DialogWrapper(project) {

    private val fileService: FileService = ServiceManager.getService(project, FileService::class.java)
    private val separator: String
    private val os: String

    private lateinit var rootPane: JPanel
    private lateinit var selectionTree: Tree

    val chosenItems = mutableListOf<String>()

    init {
        init()
        setSelectionModel(singleSelect)
        title = "Remote File Picker Dialog"

        val dirContent = fileService.getDirContent(startPath, true)
        separator = dirContent.separator
        os = dirContent.os
        val root = DefaultMutableTreeNode(RemoteFileRepresentation("root", true))
        selectionTree.isRootVisible = false
        createNodes(root, dirContent.fileTreeList)
        selectionTree.model = DefaultTreeModel(root, false)
        selectionTree.cellRenderer = FileExplorerCellTreeRenderer()
        selectionTree.addMouseListener(expandRemoteFolderListener())
        selectFromInput()
    }

    private fun setSelectionModel(singleSelect: Boolean) {
        selectionTree.selectionModel.selectionMode = if (singleSelect) {
            TreeSelectionModel.SINGLE_TREE_SELECTION
        } else {
            TreeSelectionModel.CONTIGUOUS_TREE_SELECTION
        }
    }

    private fun selectFromInput() {
        var contextItem = selectionTree.model.root as DefaultMutableTreeNode
        val nodePath = (listOf(contextItem.path.first().toString()) + startPath.split(separator)).toMutableList()
        if (os != "windows" && nodePath.size >= 2) {
            nodePath[1] = "/"
        }
        for (i in 1 until nodePath.size) {
            contextItem = contextItem.children().toList()
                .map { it as DefaultMutableTreeNode }
                .find {
                    (it.userObject as RemoteFileRepresentation).name == nodePath[i]
                } ?: break
            selectionTree.expandPath(TreePath(contextItem.path))
            selectionTree.selectionPath = TreePath(contextItem.path)
            scrollToSelectedItem(contextItem)
        }
    }

    private fun scrollToSelectedItem(node: DefaultMutableTreeNode) {
        val path = TreePath(node.path)
        val bounds = selectionTree.getPathBounds(path)
        bounds?.height = selectionTree.visibleRect.height
        selectionTree.scrollRectToVisible(bounds)
    }

    private fun createNodes(
        top: DefaultMutableTreeNode,
        fileList: MutableList<FileMeta>
    ) {
        val comparator = compareByDescending<FileMeta> { it.isDirectory }.thenBy { it.name }

        fileList.filter {
            !(onlyFoldersVisible && !it.isDirectory)
        }.sortedWith(comparator).forEach {
            val node = DefaultMutableTreeNode(RemoteFileRepresentation(it.name, it.isDirectory))

            if (top.userObject != node.userObject) {
                top.add(node)
            }
            if (it.childFilesCount != 0) {
                createNodes(node, it.childFilesList)
            }
        }
    }

    override fun getOKAction(): Action =
        object : AbstractAction("OK") {
            override fun actionPerformed(e: ActionEvent?) {
                chosenItems.clear()
                selectionTree.selectionPaths?.forEach {
                    val subList = it.path.asList().subList(1, it.path.size)
                    if (this@RemoteFilePickerDialog.os != "windows") {
                        chosenItems.add("/" + subList.subList(1, subList.size).joinToString(separator))
                    } else {
                        chosenItems.add(subList.joinToString(separator))
                    }
                }
                close(0, true)
            }
        }

    private fun expandRemoteFolderListener() = object : MouseAdapter() {
        override fun mouseClicked(e: MouseEvent?) {
            if (e?.clickCount == 2) {
                val node = selectionTree.lastSelectedPathComponent as DefaultMutableTreeNode
                val userObject = node.userObject as RemoteFileRepresentation
                if (userObject.isDirectory && node.childCount == 0) {
                    val nodePath = node.path.asList().subList(1, node.path.size)
                    val currentContent = fileService.getDirContent(nodePath.joinToString(separator), false)
                    createNodes(node, currentContent.fileTreeList.first().childFilesList)
                    if (currentContent.isEmpty()) {
                        userObject.empty = true
                    }
                }
                (selectionTree.model as DefaultTreeModel).reload(node)
                selectionTree.expandPath(TreePath(node.path))
            }
            super.mouseClicked(e)
        }
    }

    private fun FileResponse.isEmpty(): Boolean {
        return when (onlyFoldersVisible) {
            true -> this.fileTreeList.first().childFilesList.count { it.isDirectory } == 0
            else -> this.fileTreeList.first().childFilesList.count() == 0
        }
    }

    override fun createCenterPanel() = rootPane

}