package pl.dwojciechowski.ui.dialog

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.treeStructure.Tree
import pl.dwojciechowski.proto.Service
import pl.dwojciechowski.service.FileService
import pl.dwojciechowski.ui.component.FileExplorerCellTreeRenderer
import pl.dwojciechowski.ui.component.RemoteFileRepresentaton
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.Action
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeSelectionModel

class RemoteFilePickerDialog(
    project: Project,
    singleSelect: Boolean = true,
    private val onlyFoldersVisible: Boolean = true
) : DialogWrapper(project) {

    private val fileService: FileService = ServiceManager.getService(project, FileService::class.java)

    private lateinit var rootPane: JPanel
    private lateinit var selectionTree: Tree

    val chosenItems = mutableListOf<String>()

    init {
        init()

        val dirContent = fileService.getDirContent("")
        val first = dirContent.fileTreeList.first()
        val top = DefaultMutableTreeNode(RemoteFileRepresentaton(first.name, first.isDirectory))
        createNodes(top, first.childFilesList)
        selectionTree.model = DefaultTreeModel(top, false)
        selectionTree.cellRenderer = FileExplorerCellTreeRenderer()
        if (singleSelect) {
            selectionTree.selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
        }
    }

    private fun createNodes(
        top: DefaultMutableTreeNode,
        fileList: MutableList<Service.FileMeta>
    ) {
        val comparator = compareByDescending<Service.FileMeta> { it.isDirectory }.thenBy { it.name }

        fileList.filter {
            !(onlyFoldersVisible && !it.isDirectory)
        }.sortedWith(comparator).forEach {
            val node = DefaultMutableTreeNode(RemoteFileRepresentaton(it.name, it.isDirectory))

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
                    chosenItems.add(it.path.joinToString("/"))
                }
                close(0,true)
            }
        }

    override fun createCenterPanel() = rootPane

}