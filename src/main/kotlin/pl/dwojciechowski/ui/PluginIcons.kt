package pl.dwojciechowski.ui

import com.intellij.icons.AllIcons
import com.intellij.ui.SizedIcon
import com.intellij.util.IconUtil
import javax.swing.Icon

object PluginIcons {

    val CONFIRMATION = AllIcons.Actions.Commit
    val RUNNING = AllIcons.RunConfigurations.TestState.Run
    val ERROR = AllIcons.General.BalloonError
    val WARNING = AllIcons.General.BalloonWarning

    fun scaleToSize(icon: Icon, targetSize: Int): Icon {
        val scale = targetSize / icon.iconHeight.toDouble().toFloat()
        val sizedIcon = SizedIcon(icon, targetSize, targetSize).scale(scale)
        return IconUtil.toSize(sizedIcon, targetSize, targetSize)
    }

}