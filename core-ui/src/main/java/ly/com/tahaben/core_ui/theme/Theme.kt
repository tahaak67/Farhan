package ly.com.tahaben.core_ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ly.com.tahaben.core_ui.Black
import ly.com.tahaben.core_ui.DarkYellow
import ly.com.tahaben.core_ui.DarkYellowDark
import ly.com.tahaben.core_ui.DarkerYellow
import ly.com.tahaben.core_ui.DarkerYellowDark
import ly.com.tahaben.core_ui.Page
import ly.com.tahaben.core_ui.PageDark

private val DarkColorPalette = darkColorScheme(
    primary = DarkYellowDark,
    secondary = DarkerYellowDark,
    tertiary = PageDark,
    background = Black,
    onBackground = Color.White,//
    surface = Black,
    onSurface = Color.White,//
    onPrimary = Black,
    onSecondary = Black,
)

private val LightColorPalette = lightColorScheme(
    primary = DarkYellow,
    secondary = DarkerYellow,
    tertiary = Page,
    background = Color.White,
    onBackground = Black,
    surface = Color.White,
    onSurface = Black,
    onPrimary = Color.White,
    onSecondary = Color.White,
)

@Composable
fun FarhanTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}