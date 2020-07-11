package pl.dwojciechowski.ui.component.button

import com.intellij.icons.AllIcons
import javax.swing.JToggleButton
import javax.swing.text.JTextComponent

class AutoScrollButton : JToggleButton() {

    init {
        icon = AllIcons.General.ZoomOut
        selectedIcon = AllIcons.General.AutoscrollFromSource
    }

    fun link(startValue: Boolean, textComponent: JTextComponent, onEvent: (isSelected: Boolean) -> Unit) {
        isSelected = startValue
        textComponent.autoscrolls = isSelected
        addActionListener {
            textComponent.autoscrolls = isSelected
            onEvent(isSelected)
        }
    }
}