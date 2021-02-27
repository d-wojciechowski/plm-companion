package pl.dwojciechowski.configuration

import com.intellij.AbstractBundle
import org.jetbrains.annotations.PropertyKey

private const val BUNDLE_PATH = "CommonValues"

object CommonValues : AbstractBundle(BUNDLE_PATH) {

    override fun getMessage(@PropertyKey(resourceBundle = BUNDLE_PATH) key: String, vararg params: Any): String {
        return super.getMessage(key, *params)
    }

}