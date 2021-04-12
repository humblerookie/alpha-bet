package dev.anvith.alphabet

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.parents
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlText
import com.intellij.psi.xml.XmlToken
import kotlin.math.roundToInt

class GenerateIntentionColor : PsiElementBaseIntentionAction(), IntentionAction {

    override fun getFamilyName(): String {
        return FAMILY_NAME
    }

    override fun getText(): String {
        return NAME
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        return element is XmlToken && element.parents.any { it is XmlTag && it.name == COLOR_TAG && it.parentTag?.name == RES_TAG }
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val colorTag = element.parents.first { it is XmlTag && it.name == COLOR_TAG }
        val percentageString = Messages.showInputDialog(project, PERCENTAGE, NAME, null)
        val percentages = percentageString.orEmpty().trim().split(",")
        val modifiedColorTag = colorTag.copy() as XmlTag
        val attribute = modifiedColorTag.children.first { it is XmlAttribute && it.name == NAME_ATTR } as XmlAttribute
        val colorValue = modifiedColorTag.children.first { it is XmlText && it.value.startsWith("#") } as XmlText

        percentages.reversed().forEach {
            addAlphaVariant(project, modifiedColorTag, attribute, colorTag, colorValue, it)
        }
    }

    private fun addAlphaVariant(
        project: Project,
        modifiedColorTag: XmlTag,
        attribute: XmlAttribute,
        colorTag: PsiElement,
        colorValue: XmlText,
        percentageString: String?
    ) {
        val percentage = percentageString.asNumeric()
        if (percentage != null && (colorValue.isValidColor())) {
            attribute.setValue(attribute.getBaseName() + "_$percentage")
            val alpha = "%02x".format((255 * percentage / 100f).roundToInt()).toUpperCase()
            colorValue.value = "#${alpha}${colorValue.getBaseColor()}"

            val comment = getCommentTag(project, attribute, percentage)
            val anchor = (colorTag as XmlTag).getAnchor()
            colorTag.parent.addAfter(comment.copy(), anchor)
            colorTag.parent.addAfter(modifiedColorTag, anchor)
        }
    }

    /***
     * Creates a
     * @see XmlComment tag by creating a temporary memory file.
     * This is the only clean way to generate xml dom entities.
     * */
    private fun getCommentTag(project: Project, attribute: XmlAttribute, percentage: Int): PsiElement {
        val content = "<!--${attribute.getBaseName()} with $percentage% opacity-->"
        val tempFile = PsiFileFactory.getInstance(project).createFileFromText(XMLLanguage.INSTANCE, content) as XmlFile
        return tempFile.children.first().firstChild
    }

    companion object {
        const val NAME = "Generate alpha variant"
        const val FAMILY_NAME = "XML"
        const val COLOR_TAG = "color"
        const val RES_TAG = "resources"
        const val NAME_ATTR = "name"
        const val PERCENTAGE = "Percentage(multiple values are separated by comma):"
    }
}
