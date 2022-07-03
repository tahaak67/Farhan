package ly.com.tahaben.core_ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ly.com.tahaben.core_ui.*

private val DarkColorPalette = darkColors(
    primary = White,
    primaryVariant = DarkYellow,
    secondary = Page
)

private val LightColorPalette = lightColors(
    primary = DarkYellow,
    primaryVariant = DarkerYellow,
    secondary = Page,
    background = Color.White,
    onBackground = Black,
    surface = Color.White,
    onSurface = Black,
    onPrimary = Color.White,
    onSecondary = Color.White,
)

@Composable
fun FarhanTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        LightColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}