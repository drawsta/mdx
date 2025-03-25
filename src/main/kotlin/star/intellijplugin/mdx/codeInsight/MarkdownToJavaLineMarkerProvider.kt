package star.intellijplugin.mdx.codeInsight

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.NavigatablePsiElement
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

    private val classRegex = """@java-class\s+([\w.]+)""".toRegex()

    private val methodRegex = """@java-method\s+([\w.]+)#(\w+)""".toRegex()

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element !is MarkdownParagraph) return null
        val text = element.text
        val project = element.project
        val psiFacade = JavaPsiFacade.getInstance(project)

        // 处理 @java-class
        classRegex.find(text)?.let { match ->
            val className = match.groupValues[1]
            val psiClass = psiFacade.findClass(className, GlobalSearchScope.allScope(project)) ?: return null
            return createLineMarker(element, "跳转到 $className", psiClass)
        }

        // 处理 @java-method
        methodRegex.find(text)?.let { match ->
            val className = match.groupValues[1]
            val methodName = match.groupValues[2]
            val psiClass = psiFacade.findClass(className, GlobalSearchScope.allScope(project)) ?: return null
            val psiMethod = psiClass.findMethodsByName(methodName, false).firstOrNull() ?: return null
            return createLineMarker(element, "跳转到 $className#$methodName", psiMethod)
        }

        return null
    }

    private fun createLineMarker(
        element: PsiElement,
        tooltip: String,
        target: NavigatablePsiElement
    ): LineMarkerInfo<PsiElement> {
        return LineMarkerInfo(
            element, element.textRange, AllIcons.Nodes.Class,
            { tooltip }, JavaClassNavigationHandler(target),
            GutterIconRenderer.Alignment.LEFT, { tooltip }
        )
    }
}

class JavaClassNavigationHandler(private val target: NavigatablePsiElement) : GutterIconNavigationHandler<PsiElement> {
    override fun navigate(event: MouseEvent?, element: PsiElement?) {
        target.navigate(true)
    }
}
