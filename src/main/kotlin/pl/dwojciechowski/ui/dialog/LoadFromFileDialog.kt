package pl.dwojciechowski.ui.dialog

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBList
import com.intellij.ui.treeStructure.Tree
import pl.dwojciechowski.proto.Service
import pl.dwojciechowski.service.FileService
import pl.dwojciechowski.ui.component.FileExplorerCellTreeRenderer
import pl.dwojciechowski.ui.component.RemoteFileRepresentaton
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel


class LoadFromFileDialog(project: Project) : DialogWrapper(project) {

    private val fileService: FileService = ServiceManager.getService(project, FileService::class.java)

    private lateinit var rootPane: JPanel

    private lateinit var tree1: Tree
    private lateinit var list1: JBList<String>

    init {
        init()

        val dirContent = fileService.getDirContent("")
        val first = dirContent.fileTreeList.first()
        val top = DefaultMutableTreeNode(RemoteFileRepresentaton(first.name, first.isDirectory))
        createNodes(top, first.childFilesList)
        tree1.model = DefaultTreeModel(top, false)
        tree1.cellRenderer = FileExplorerCellTreeRenderer()
    }

    private fun createNodes(
        top: DefaultMutableTreeNode,
        fileList: MutableList<Service.FileMeta>
    ) {
        val comparator = compareByDescending<Service.FileMeta> { it.isDirectory }.thenBy { it.name }

        fileList.sortedWith(comparator).forEach {
            val node = DefaultMutableTreeNode(RemoteFileRepresentaton(it.name, it.isDirectory))

            if (top.userObject != node.userObject) {
                top.add(node)
            }
            if (it.childFilesCount != 0) {
                createNodes(node, it.childFilesList)
            }
        }
    }

    override fun createCenterPanel() = rootPane

}