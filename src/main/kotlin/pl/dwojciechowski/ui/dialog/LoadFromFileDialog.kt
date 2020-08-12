package pl.dwojciechowski.ui.dialog

import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBList
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.i18n.PluginBundle.getMessage
import pl.dwojciechowski.model.CommandBean
import pl.dwojciechowski.service.IdeControlService
import pl.dwojciechowski.service.RemoteService
import pl.dwojciechowski.ui.component.CustomVirtualFileListCellRenderer
import pl.dwojciechowski.ui.component.EtchedTitleBorder
import pl.dwojciechowski.ui.component.RunConfigurationComboBox
import java.awt.event.ActionEvent
import javax.swing.*

class LoadFromFileDialog(
    private val project: Project,
    private val vFiles: List<VirtualFile>
) : DialogWrapper(project), Disposable {

    private val config = ServiceManager.getService(project, PluginConfiguration::class.java)
    private val commandService = ServiceManager.getService(project, RemoteService::class.java)
    private val ideControlService = ServiceManager.getService(project, IdeControlService::class.java)

    lateinit var content: JPanel

    private lateinit var wtHomePanel: JPanel
    private lateinit var targetContainerPanel: JPanel
    private lateinit var preRunConfigPanel: JPanel

    private lateinit var fileList: JBList<VirtualFile>
    private lateinit var listModel: DefaultListModel<VirtualFile>

    private lateinit var folderPickerButton: JButton
    private lateinit var folderPathTextFile: JTextField

    private lateinit var rootRadio: JRadioButton
    private lateinit var orgRadio: JRadioButton
    private lateinit var productRadio: JRadioButton
    private lateinit var libraryRadio: JRadioButton
    private lateinit var projectRadio: JRadioButton

    private lateinit var runConfigurationComboBox: RunConfigurationComboBox

    private lateinit var orgTextField: JTextField
    private lateinit var containerTextField: JTextField

    val chosenItems = mutableListOf<VirtualFile>()

    fun createUIComponents() {
        listModel = DefaultListModel()
        vFiles.forEach {
            listModel.addElement(it)
        }

        fileList = JBList(listModel)
        fileList.cellRenderer = CustomVirtualFileListCellRenderer(project)
        fileList.setSelectionInterval(0, listModel.size())

        runConfigurationComboBox = RunConfigurationComboBox(project)
    }

    init {
        setUpRadioButtons()

        init()
        title = getMessage("ui.dialog.lff.title")

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
        wtHomePanel.border = EtchedTitleBorder(getMessage("ui.dialog.lff.wthome"))
        targetContainerPanel.border = EtchedTitleBorder(getMessage("ui.dialog.lff.container"))
        preRunConfigPanel.border = EtchedTitleBorder(getMessage("ui.dialog.lff.runconf.name"))

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
                    runCommand(finalCommand)
                }
                dispose()
                close(OK_EXIT_CODE)
            }
        }

    private fun runCommand(finalCommand: String) {
        val runConfig = runConfigurationComboBox.getSelectedConfiguration().value
        if (runConfig == null) {
            ideControlService.withAutoOpen {
                commandService.executeStreaming(CommandBean(getMessage("ui.dialog.lff.command.name"), finalCommand))
            }
        } else {
            val executor = DefaultRunExecutor.getRunExecutorInstance()
            val builder = ExecutionEnvironmentBuilder.create(executor, runConfig)
            val executorEnv = builder.contentToReuse(null).dataContext(null).activeTarget().build()

            execAsync(executorEnv, finalCommand)
        }

    }

    private fun execAsync(executorEnv: ExecutionEnvironment, finalCommand: String) {
        ProgramRunnerUtil.executeConfigurationAsync(
            executorEnv, true, true
        ) {
            it.processHandler?.addProcessListener(object : ProcessAdapter() {
                override fun processTerminated(event: ProcessEvent) {
                    super.processTerminated(event)
                    ideControlService.withAutoOpen {
                        commandService.executeStreaming(CommandBean(getMessage("ui.dialog.lff.command.name"), finalCommand))
                    }
                }
            })
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
