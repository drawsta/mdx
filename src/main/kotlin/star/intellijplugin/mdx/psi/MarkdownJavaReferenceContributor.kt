package star.intellijplugin.mdx.psi

import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.ProcessingContext

/**
 * not work
 *
 * @author drawsta
 * @LastModified: 2025-03-19
 * @since 2025-03-19
 */
class MarkdownJavaReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(PsiElement::class.java),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
                    val text = element.text
                    if (text.startsWith("@java-class")) {
                        val className = text.substringAfter("@java-class ").trim()
                        return arrayOf(JavaClassReference(element, className))
                    }
                    return PsiReference.EMPTY_ARRAY
                }
            }
        )
    }
}

class JavaClassReference(element: PsiElement, private val className: String) : PsiReferenceBase<PsiElement>(element, TextRange(0, element.textLength)), PsiPolyVariantReference {
    override fun resolve(): PsiElement? {
        return JavaPsiFacade.getInstance(element.project)
            .findClass(className, GlobalSearchScope.allScope(element.project))
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val clazz = resolve() ?: return ResolveResult.EMPTY_ARRAY
        return arrayOf(PsiElementResolveResult(clazz))
    }
}
