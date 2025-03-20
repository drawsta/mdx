package star.intellijplugin.mdx

import com.intellij.DynamicBundle
import org.jetbrains.annotations.PropertyKey

/**
 * @author drawsta
 * @since 2025-03-15
 */
object MarkdownXBundleBundle {
    // 绑定到 src/main/resources/messages/MarkdownXBundle.properties
    private const val BUNDLE: String = "messages.MarkdownXBundle"

    private val INSTANCE = DynamicBundle(MarkdownXBundleBundle::class.java, BUNDLE)

    fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String {
        return INSTANCE.getMessage(key, *params)
    }
}
