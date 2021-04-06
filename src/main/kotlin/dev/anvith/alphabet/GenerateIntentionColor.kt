package dev.anvith.alphabet

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parents
import com.intellij.psi.xml.*
import javax.swing.ImageIcon
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
        val file = element.containingFile as XmlFile
        val percentageString = Messages.showInputDialog(project, PERCENTAGE, NAME,null)

        val modifiedColorTag = colorTag.copy() as XmlTag
        val attribute = modifiedColorTag.children.first { it is XmlAttribute && it.name == NAME_ATTR } as XmlAttribute
        val colorValue = modifiedColorTag.children.first { it is XmlText && it.value.startsWith("#") } as XmlText

        val percentage = percentageString.asNumeric()
        if (percentage != null && (colorValue.isValidColor())) {
            attribute.setValue(attribute.value + "_$percentage")
            val alpha = "%02x".format((255 * percentage / 100f).roundToInt()).toUpperCase()
            colorValue.value = "#${alpha}${colorValue.getBaseColor()}"
            file.addAfter(modifiedColorTag, colorTag)
        }
    }

    companion object {
        const val NAME = "Generate alpha variant"
        const val FAMILY_NAME = "XML"
        const val COLOR_TAG = "color"
        const val RES_TAG = "resources"
        const val NAME_ATTR = "name"
        const val PERCENTAGE = "Percentage: "
    }
}


