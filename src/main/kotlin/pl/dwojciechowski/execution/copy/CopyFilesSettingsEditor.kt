package pl.dwojciechowski.execution.copy

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextComponentAccessor
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.ui.TextFieldWithAutoCompletion
import com.intellij.ui.components.JBList
import pl.dwojciechowski.configuration.CommonValues
import pl.dwojciechowski.configuration.ProjectPluginConfiguration
import pl.dwojciechowski.ui.component.TextFieldBrowseCompletition
import java.nio.file.Paths
import javax.swing.DefaultListModel
import javax.swing.JButton
import javax.swing.JPanel

class CopyFilesSettingsEditor(val project: Project) : SettingsEditor<CopyFilesRunConfig>() {

    private val config = ServiceManager.getService(project, ProjectPluginConfiguration::class.java)

    private lateinit var myPanel: JPanel

    private lateinit var folderName: TextFieldBrowseCompletition
    private lateinit var ignoredList: TextFieldWithAutoCompletion<String>
    private lateinit var addButton: JButton
    private lateinit var configList: JBList<FolderConfig>
    private lateinit var configModel: DefaultListModel<FolderConfig>

    fun createUIComponents() {
        folderName = TextFieldBrowseCompletition(project, mutableListOf(), true)
        ignoredList = TextFieldWithAutoCompletion.create(project, mutableListOf(), true, "")

        configModel = DefaultListModel<FolderConfig>()
        configList = JBList(configModel)
    }

    init {
        val basePath = project.basePath ?: ""
        ignoredList.setVariants(CommonValues.getMessage("ignored-extension-pool").split(","))

        initializeFolderNameComponent(basePath)

        addButton.addActionListener {
            val folderConfig =
                FolderConfig(folderName.text, config.lffFolder, ignoredList.text.split(" ").toMutableList())
            configModel.addElement(folderConfig)
        }
    }

    private fun initializeFolderNameComponent(basePath: String) {
        val folderList = VirtualFileManager.getInstance().findFileByNioPath(Paths.get(basePath))?.let {
            flattenFolderStructure(it)
                .map { path ->
                    Paths.get(basePath).relativize(Paths.get(path)).toString()
                }.toList()
        } ?: mutableListOf()

        folderName.setAutoCompletionItems(folderList)
        val descriptor = FileChooserDescriptor(false, true, false, false, false, false)

        val accessor = object : TextComponentAccessor<TextFieldWithAutoCompletion<String>> {
            override fun getText(component: TextFieldWithAutoCompletion<String>?): String {
                return basePath + component?.text
            }

            override fun setText(component: TextFieldWithAutoCompletion<String>?, text: String) {
                component?.text = Paths.get(basePath).relativize(Paths.get(text)).toString()
            }
        }

        folderName.addBrowseFolderListener(
            "Folder picker", "Select folders", project, descriptor, accessor
        )
    }

    private fun flattenFolderStructure(file: VirtualFile): MutableList<String> {
        if (file.isDirectory) {
            val children = file.children.flatMap { flattenFolderStructure(it) }.toMutableList()
            children.add(file.path)
            return children
        }
        return mutableListOf()
    }


    override fun resetEditorFrom(s: CopyFilesRunConfig) {
        s.settings.folderConfigs.forEach(configModel::addElement)
    }

    override fun applyEditorTo(s: CopyFilesRunConfig) {
        s.settings.folderConfigs.addAll(configModel.elements().toList())
        s.settings.async = true
    }

    override fun createEditor() = myPanel

}