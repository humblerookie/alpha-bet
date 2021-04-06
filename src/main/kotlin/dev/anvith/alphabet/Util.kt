package dev.anvith.alphabet

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