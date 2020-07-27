package pl.dwojciechowski.ui.panel

import com.intellij.openapi.project.Project
import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.panel

internal class PLMCompanionPanel(project: Project) {

    private val customCommandPanel = CommandSubPanel(project).content
    private val fatButtonPanel = FatButtonPanel(project).content

    val content = panel {
        row {
            fatButtonPanel(CCFlags.growX)
        }
        row {
            customCommandPanel(CCFlags.grow, CCFlags.pushY)
        }
    }

}
