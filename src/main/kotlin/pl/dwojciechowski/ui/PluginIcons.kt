package pl.dwojciechowski.ui

import com.intellij.icons.AllIcons
import com.intellij.util.IconUtil
import javax.swing.Icon

object PluginIcons {

    val CONFIRMATION = AllIcons.Actions.Commit
    val RUNNING = AllIcons.RunConfigurations.TestState.Run
    val ERROR = AllIcons.General.BalloonError
    val WARNING = AllIcons.General.BalloonWarning

    fun scaleToSize(icon: Icon, targetSize: Int): Icon {
        val iconHeight = icon.iconHeight
        return IconUtil.toSize(IconUtil.scale(icon, (targetSize / iconHeight.toDouble())), targetSize, targetSize)
    }

}