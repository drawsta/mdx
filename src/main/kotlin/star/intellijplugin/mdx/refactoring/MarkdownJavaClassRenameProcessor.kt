package star.intellijplugin.mdx.refactoring

import com.intellij.psi.*
import com.intellij.refactoring.listeners.RefactoringElementListener
import com.intellij.refactoring.rename.RenamePsiElementProcessor
import com.intellij.usageView.UsageInfo

/**
 * @author drawsta
 * @LastModified: 2025-03-19
 * @since 2025-03-19
 */
class MarkdownJavaClassRenameProcessor: RenamePsiElementProcessor() {
    override fun canProcessElement(element: PsiElement): Boolean {
        return element is PsiClass
    }

    override fun renameElement(
        element: PsiElement,
        newName: String,
        usages: Array<UsageInfo>,
        listener: RefactoringElementListener?
    ) {
        super.renameElement(element, newName, usages, listener)
        val project = element.project
        val psiManager = PsiManager.getInstance(project)
        val files = psiManager.findFile(element.containingFile.virtualFile)
        files?.accept(object : PsiRecursiveElementVisitor() {
            override fun visitElement(element: PsiElement) {
                if (element.text.startsWith("@java-class")) {
                    val oldClassName = element.text.substringAfter("@java-class ").trim()
                    if (oldClassName == (element as PsiNamedElement).name) {
                        element.replace(PsiElementFactory.getInstance(project).createCommentFromText("@java-class $newName", element))
                    }
                }
                super.visitElement(element)
            }
        })
    }
}
