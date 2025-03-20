package star.intellijplugin.mdx.codeInsight

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.ide.DataManager
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import org.intellij.plugins.markdown.MarkdownIcons
import org.intellij.plugins.markdown.lang.MarkdownFileType
import java.awt.event.MouseEvent

/**
 * @author drawsta
 * @LastModified: 2025-03-19
 * @since 2025-03-19
 */
class JavaToMarkdownLineMarkerProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element !is PsiClass) return null  // 仅处理 Java 类

        val className = element.qualifiedName ?: return null
        val project = element.project

        // 查找所有 Markdown 文件中是否有 "@java-class <className>"
        val markdownFiles = findMarkdownFilesReferencingClass(project, className)

        if (markdownFiles.isEmpty()) return null  // 没有引用该类的 Markdown 文件

        return LineMarkerInfo(
            element.nameIdentifier ?: return null,  // Java 类的名称
            element.nameIdentifier!!.textRange,
            MarkdownIcons.MarkdownPlugin,
            { "跳转到引用该类的 Markdown 文档" },
            MarkdownNavigationHandler(markdownFiles),
            GutterIconRenderer.Alignment.LEFT,
            { "跳转到引用该类的 Markdown 文档" }
        )
    }

    private fun findMarkdownFilesReferencingClass(project: Project, className: String): List<PsiFile> {
        val markdownFiles = mutableListOf<PsiFile>()
        val scope = GlobalSearchScope.allScope(project)

        FileTypeIndex.getFiles(MarkdownFileType.INSTANCE, scope).forEach { virtualFile ->
            val psiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: return@forEach
            if (psiFile.text.contains("@java-class $className")) {
                markdownFiles.add(psiFile)
            }
        }

        return markdownFiles
    }
}

class MarkdownNavigationHandler(private val markdownFiles: List<PsiFile>) : GutterIconNavigationHandler<PsiElement> {
    override fun navigate(event: MouseEvent?, element: PsiElement?) {
        if (markdownFiles.size == 1) {
            // 只有一个 Markdown 文件引用，直接跳转
            navigateToMarkdownFile(markdownFiles.first())
        } else {
            // 多个 Markdown 文件引用，弹出选择框
            showMarkdownSelectionPopup(event, markdownFiles)
        }
    }

    private fun navigateToMarkdownFile(markdownFile: PsiFile) {
        markdownFile.navigate(true)
    }

    private fun showMarkdownSelectionPopup(event: MouseEvent?, markdownFiles: List<PsiFile>) {
        val items = markdownFiles.map { file ->
            Pair(file, file.name)
        }

        if (event != null) {
            JBPopupFactory.getInstance()
                .createPopupChooserBuilder(items)
                .setTitle("选择引用该类的 Markdown 文档")
                .setMovable(true)
                .setResizable(true)
                .setRenderer { list, value, _, isSelected, _ ->
                    panel {
                        row {
                            icon(AllIcons.FileTypes.Text)
                            label(value.second).applyToComponent {
                                foreground = if (isSelected) list.selectionForeground else list.foreground
                            }
                        }
                    }.apply {
                        border = JBUI.Borders.empty(4)
                        background = if (isSelected) list.selectionBackground else list.background
                    }
                }
                .setItemChosenCallback { selectedPair ->
                    selectedPair.first.navigate(true)
                }
                .createPopup()
                .showInBestPositionFor(DataManager.getInstance().getDataContext(event.component))
        }
    }
}
