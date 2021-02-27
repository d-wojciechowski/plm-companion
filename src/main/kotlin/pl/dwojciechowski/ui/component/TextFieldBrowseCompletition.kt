package pl.dwojciechowski.ui.component

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComponentWithBrowseButton
import com.intellij.ui.TextAccessor
import com.intellij.ui.TextFieldWithAutoCompletion
import java.awt.event.ActionListener

class TextFieldBrowseCompletition(
    project: Project,
    items: MutableCollection<String>,
    showCompletionHint: Boolean = false,
    text: String = "",
    browseListener: ActionListener = ActionListener { }
) : ComponentWithBrowseButton<TextFieldWithAutoCompletion<String>>(
    TextFieldWithAutoCompletion.create(project, items, showCompletionHint, text), browseListener
), TextAccessor {

    override fun getText(): String {
        return childComponent.text
    }

    fun setAutoCompletionItems(items: List<String>) {
        childComponent.setVariants(items)
    }

    override fun setText(text: String?) {
        childComponent.setText(text)
    }

}