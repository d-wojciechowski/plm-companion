package pl.dwojciechowski.ui.dialog

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBList
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.model.CommandBean
import pl.dwojciechowski.service.WncConnectorService
import pl.dwojciechowski.ui.component.CustomVirtualFileListCellRenderer
import java.awt.event.ActionEvent
import javax.swing.*

class LoadFromFileDialog(
    private val project: Project,
    private val vfiles: List<VirtualFile>
) : DialogWrapper(project), org.picocontainer.Disposable {

    private val config: PluginConfiguration = ServiceManager.getService(project, PluginConfiguration::class.java)
    private val commandService: WncConnectorService =
        ServiceManager.getService(project, WncConnectorService::class.java)

    lateinit var content: JPanel

    private lateinit var fileList: JBList<VirtualFile>
    private lateinit var listModel: DefaultListModel<VirtualFile>

    private lateinit var folderPickerButton: JButton
    private lateinit var folderPathTextFile: JTextField

    private lateinit var rootRadio: JRadioButton
    private lateinit var orgRadio: JRadioButton
    private lateinit var productRadio: JRadioButton
    private lateinit var libraryRadio: JRadioButton
    private lateinit var projectRadio: JRadioButton

    private lateinit var orgTextField: JTextField
    private lateinit var containerTextField: JTextField

    val chosenItems = mutableListOf<VirtualFile>()

    fun createUIComponents() {
        listModel = DefaultListModel()
        vfiles.forEach {
            listModel.addElement(it)
        }

        fileList = JBList(listModel)
        fileList.cellRenderer = CustomVirtualFileListCellRenderer(project)
        fileList.setSelectionInterval(0, listModel.size())
    }

    init {
        setUpRadioButtons()

        init()
        title = "Load From File Dialog"

        folderPathTextFile.text = config.lffFolder
        containerTextField.text = config.lffContName
        orgTextField.text = config.lffOrgName

        folderPickerButton.icon = AllIcons.General.OpenDisk
        folderPickerButton.addActionListener {
            val remoteFilePickerDialog = RemoteFilePickerDialog(project, folderPathTextFile.text)
            if (remoteFilePickerDialog.showAndGet()) {
                folderPathTextFile.text = remoteFilePickerDialog.chosenItems.first()
                config.lffFolder = folderPathTextFile.text
            }
        }

    }

    private fun setUpRadioButtons() {
        val btnGrp = ButtonGroup()
        btnGrp.add(rootRadio.onClickFieldStatus(contEnabled = false, orgEnabled = false, ordinal = 0))
        btnGrp.add(orgRadio.onClickFieldStatus(contEnabled = false, orgEnabled = true, ordinal = 1))
        btnGrp.add(productRadio.onClickFieldStatus(contEnabled = true, orgEnabled = true, ordinal = 2))
        btnGrp.add(libraryRadio.onClickFieldStatus(contEnabled = true, orgEnabled = true, ordinal = 3))
        btnGrp.add(projectRadio.onClickFieldStatus(contEnabled = true, orgEnabled = true, ordinal = 4))
        btnGrp.elements.iterator().withIndex().forEach {
            if (it.index == config.lffTarget) {
                it.value.isSelected = true
                it.value.actionListeners[0].actionPerformed(ActionEvent("", 0, ""))
            }
        }
    }

    private fun JRadioButton.onClickFieldStatus(
        contEnabled: Boolean,
        orgEnabled: Boolean,
        ordinal: Int
    ): JRadioButton {
        this.addActionListener {
            containerTextField.isEnabled = contEnabled
            orgTextField.isEnabled = orgEnabled
            config.lffTarget = ordinal
        }
        return this
    }

    override fun createCenterPanel() = content
    override fun dispose() {
        config.lffContName = containerTextField.text
        config.lffOrgName = orgTextField.text
        super.dispose()
    }

    override fun getOKAction(): Action =
        object : AbstractAction("OK") {
            override fun actionPerformed(e: ActionEvent?) {
                chosenItems.addAll(fileList.selectedValuesList)
                val finalCommand = chosenItems.joinToString(" && ") { vf ->
                    val parentPath = project.basePath?.length?.let { vf.path.substring(it) }
                    "windchill wt.load.LoadFromFile -u ${config.login} -p ${config.passwd}" +
                            " -d ${config.lffFolder + parentPath} -CONT_PATH ${getContPath()}"
                }
                if (finalCommand.isNotEmpty()) {
                    commandService.executeStreaming(CommandBean("Load from file", finalCommand))
                }
                dispose()
                close(OK_EXIT_CODE)
            }
        }

    private fun getContPath() =
        ContPath.values()[config.lffTarget].getContPath(orgTextField.text, containerTextField.text)

    override fun getCancelAction(): Action =
        object : AbstractAction("Cancel") {
            override fun actionPerformed(e: ActionEvent?) {
                dispose()
                close(CANCEL_EXIT_CODE)
            }
        }

    private enum class ContPath(private val contPath: String) {
        ROOT("\"/\""),
        ORG("\"/wt.inf.container.OrgContainer=%s\""),
        PRODUCT("\"/wt.inf.container.OrgContainer=%s/wt.pdmlink.PDMLinkProduct=%s\""),
        LIBRARY("\"/wt.inf.container.OrgContainer=%s/wt.inf.library.WTLibrary=%s\""),
        PROJECT("\"/wt.inf.container.OrgContainer=%s/wt.projmgmt.admin.Project2=%s\"");

        fun getContPath(orgName: String = "", contName: String = "") = String.format(contPath, orgName, contName)
    }

}
