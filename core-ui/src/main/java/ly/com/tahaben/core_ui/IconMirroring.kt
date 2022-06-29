package ly.com.tahaben.core_ui

import android.util.LayoutDirection
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.core.text.layoutDirection
import java.util.*

fun Modifier.mirror(): Modifier {
    if (Locale.getDefault().layoutDirection == LayoutDirection.RTL)
        return this.scale(scaleX = -1f, scaleY = 1f)
    else
        return this
}