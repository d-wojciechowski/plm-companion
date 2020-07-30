package pl.dwojciechowski.i18n

import com.intellij.AbstractBundle
import org.jetbrains.annotations.PropertyKey

private const val BUNDLE_PATH = "i18n.PluginBundle"

object PluginBundle : AbstractBundle(BUNDLE_PATH) {

    override fun getMessage(@PropertyKey(resourceBundle = BUNDLE_PATH) key: String, vararg params: Any): String {
        return super.getMessage(key, *params)
    }

}