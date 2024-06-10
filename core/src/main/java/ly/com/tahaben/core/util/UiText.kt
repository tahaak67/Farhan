package ly.com.tahaben.core.util

import android.content.Context
import androidx.annotation.StringRes
import java.text.DecimalFormat

sealed class UiText {
    data class DynamicString(val text: String) : UiText()
    data class StringResource(val resId: Int) : UiText()
    data class TimeFormatString(
        val textHrs: Int,
        @StringRes
        val resIdh: Int,
        val textMin: Int,
        @StringRes
        val resIdm: Int
    ) : UiText()

    data class MixedString(val text: Int, val resId: Int) : UiText()

    fun asString(context: Context): String {
        val decimalFormat = DecimalFormat.getInstance()
        return when (this) {
            is DynamicString -> text
            is StringResource -> context.getString(resId)
            is TimeFormatString -> decimalFormat.format(textHrs) + context.getString(resIdh) + decimalFormat.format(
                textMin
            ) + context.getString(
                resIdm
            )

            is MixedString -> decimalFormat.format(text) + context.getString(resId)
        }
    }


}
