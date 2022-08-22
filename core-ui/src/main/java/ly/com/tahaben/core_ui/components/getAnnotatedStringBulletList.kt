package ly.com.tahaben.core_ui.components

import androidx.compose.ui.text.buildAnnotatedString

fun getAnnotatedStringBulletList(
    messages: List<String>,
    bullet: String = "\u2022"
) = buildAnnotatedString {
    messages.forEach {
        append(bullet)
        append("\t\t")
        append(it)
        append("\n")
    }
}