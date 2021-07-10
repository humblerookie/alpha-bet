package dev.anvith.alphabet

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlComment
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlText

fun String?.asNumeric(): Int? {
    if (!isNullOrBlank()) {
        val num = toIntOrNull()
        if (num != null) {
            return when (num) {
                in 0..100 -> num
                in Int.MIN_VALUE..0 -> 0
                else -> 100
            }
        }
    }
    return null
}

fun XmlText.isValidColor(): Boolean {
    return value.trim().run {
        length == 7 || length == 9
    }
}

fun XmlText.getBaseColor(): String {
    return value.trim().run {
        substring(if (length == 7) 1 else 3)
    }
}

fun XmlAttribute.getBaseName(): String {
    val index = value?.lastIndexOf("_") ?: -1
    return if (index > -1) {
        value!!.substring(0, index)
    } else {
        value.orEmpty()
    }
}

fun XmlTag.getAnchor(): PsiElement {
    var next: PsiElement = this
    while (next.nextSibling is XmlComment && next.nextSibling != null) {
        next = next.nextSibling
    }
    return next
}
