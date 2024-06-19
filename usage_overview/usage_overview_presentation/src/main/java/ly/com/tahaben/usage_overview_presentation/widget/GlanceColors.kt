package ly.com.tahaben.usage_overview_presentation.widget

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.glance.material3.ColorProviders
import ly.com.tahaben.core_ui.theme.classicDarkScheme
import ly.com.tahaben.core_ui.theme.classicLightScheme
import ly.com.tahaben.core_ui.theme.darkScheme
import ly.com.tahaben.core_ui.theme.lightScheme

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 18,Jun,2024
 */


object VibrantColorScheme {
    val light = ColorProviders(
        light = lightScheme,
        dark = lightScheme
    )
    val dark = ColorProviders(
        light = darkScheme,
        dark = darkScheme
    )
    val followSystem = ColorProviders(
        light = lightScheme,
        dark = darkScheme
    )
}

object ClassicColorScheme {
    val light = ColorProviders(
        light = classicLightScheme,
        dark = classicLightScheme
    )
    val dark = ColorProviders(
        light = classicDarkScheme,
        dark = classicDarkScheme
    )
    val followSystem = ColorProviders(
        light = classicLightScheme,
        dark = classicDarkScheme
    )
}

@RequiresApi(Build.VERSION_CODES.S)
class DynamicColorScheme(context: Context) {
    val light = ColorProviders(
        light = dynamicLightColorScheme(context),
        dark = dynamicLightColorScheme(context)
    )
    val dark = ColorProviders(
        light = dynamicDarkColorScheme(context),
        dark = dynamicDarkColorScheme(context)
    )
    val followSystem = ColorProviders(
        light = dynamicLightColorScheme(context),
        dark = dynamicDarkColorScheme(context)
    )
}