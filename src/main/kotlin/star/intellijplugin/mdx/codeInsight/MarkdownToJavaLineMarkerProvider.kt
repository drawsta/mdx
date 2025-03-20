package star.intellijplugin.mdx.codeInsight

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownParagraph
import java.awt.event.MouseEvent

/**
 * @author drawsta
 * @LastModified: 2025-03-14
 * @since 2025-03-14
 */
class MarkdownToJavaLineMarkerProvider : LineMarkerProvider {

    private val regex = """@java-class\s+([\w.]+)""".toRegex()

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element !is MarkdownParagraph) return null
        val text = element.text
        val matchResult = regex.find(text) ?: return null
        val className = matchResult.groupValues[1]
        val project = element.project
        val psiClass = JavaPsiFacade.getInstance(project)
            .findClass(className, GlobalSearchScope.allScope(project)) ?: return null

        return LineMarkerInfo(
            element, element.textRange, AllIcons.Nodes.Class,
            { "跳转到 $className" }, JavaClassNavigationHandler(psiClass),
            GutterIconRenderer.Alignment.LEFT,
            { "跳转到 $className" }
        )
    }
}

class JavaClassNavigationHandler(private val psiClass: PsiClass) : GutterIconNavigationHandler<PsiElement> {
    override fun navigate(event: MouseEvent?, element: PsiElement?) {
        psiClass.navigate(true)
    }
}
