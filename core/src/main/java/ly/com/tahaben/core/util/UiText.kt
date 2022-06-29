package ly.com.tahaben.core.util

import android.content.Context

sealed class UiText {
    data class DynamicString(val text: String) : UiText()
    data class StringResource(val resId: Int) : UiText()
    data class TimeFormatString(
        val textHrs: String,
        val resIdh: Int,
        val textMin: String,
        val resIdm: Int
    ) : UiText()

    data class MixedString(val text: String, val resId: Int) : UiText()

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> text
            is StringResource -> context.getString(resId)
            is TimeFormatString -> textHrs + context.getString(resIdh) + textMin + context.getString(
                resIdm
            )
            is MixedString -> text + context.getString(resId)
        }
    }
}
