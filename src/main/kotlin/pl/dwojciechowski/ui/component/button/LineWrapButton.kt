package pl.dwojciechowski.ui.component.button

import com.intellij.icons.AllIcons
import javax.swing.JTextArea
import javax.swing.JToggleButton

class LineWrapButton : JToggleButton() {

    init {
        icon = AllIcons.General.LayoutEditorOnly
        selectedIcon = AllIcons.Actions.ToggleSoftWrap
    }

    fun link(startValue: Boolean, textComponent: JTextArea, onEvent: (isSelected: Boolean) -> Unit) {
        isSelected = startValue
        textComponent.lineWrap = isSelected
        addActionListener {
            textComponent.lineWrap = isSelected
            onEvent(isSelected)
        }
    }

}