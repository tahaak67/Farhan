package ly.com.tahaben.core_ui.util

import android.graphics.Typeface
import android.text.Html
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
fun getAnnotatedStringResource(@StringRes id: Int): AnnotatedString {
    val annotatedText = stringResource(id = id)
    val spannedText = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        Html.fromHtml(annotatedText, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(annotatedText)
    }
    val annotatedString = spannedText.toAnnotatedString()
    return annotatedString
}

private fun Spanned.toAnnotatedString(): AnnotatedString =
    buildAnnotatedString {
        val spanned = this@toAnnotatedString
        append(spanned.toString())

        getSpans(
            0,
            spanned.length,
            Any::class.java
        ).forEach { span ->
            val start = getSpanStart(span)
            val end = getSpanEnd(span)

            when (span) {
                is StyleSpan ->
                    when (span.style) {
                        Typeface.BOLD -> addStyle(
                            SpanStyle(fontWeight = FontWeight.Bold),
                            start,
                            end
                        )

                        Typeface.ITALIC -> addStyle(
                            SpanStyle(fontStyle = FontStyle.Italic),
                            start,
                            end
                        )

                        Typeface.BOLD_ITALIC -> addStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic
                            ),
                            start,
                            end
                        )
                    }

                is ForegroundColorSpan -> addStyle( // For <span style=color:blue> tag.
                    SpanStyle(color = Color.Blue),
                    start,
                    end
                )
            }
        }
    }