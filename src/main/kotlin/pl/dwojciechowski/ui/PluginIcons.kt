package pl.dwojciechowski.ui

import com.intellij.icons.AllIcons
import com.intellij.ui.IconManager
import com.intellij.ui.SizedIcon
import com.intellij.util.IconUtil
import javax.swing.Icon

object PluginIcons {

    val PLUGIN = load("/icons/plm_companion_icon16.svg")
    val PLUGIN_BIG = load("/META-INF/pluginIcon.svg")
    val CONFIRMATION = AllIcons.Actions.Commit
    val RUNNING = AllIcons.RunConfigurations.TestState.Run
    val ERROR = AllIcons.General.BalloonError
    val WARNING = AllIcons.General.BalloonWarning

    fun scaleToSize(icon: Icon, targetSize: Int): Icon {
        val scale = targetSize / icon.iconHeight.toDouble().toFloat()
        val sizedIcon = SizedIcon(icon, targetSize, targetSize).scale(scale)
        return IconUtil.toSize(sizedIcon, targetSize, targetSize)
    }

    private fun load(path: String): Icon {
        return IconManager.getInstance().getIcon(path, PluginIcons::class.java)
    }

}