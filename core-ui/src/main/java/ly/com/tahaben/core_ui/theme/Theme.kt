package ly.com.tahaben.core_ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import ly.com.tahaben.core.util.ThemeColors
import ly.com.tahaben.core_ui.backgroundDark
import ly.com.tahaben.core_ui.backgroundDarkClassic
import ly.com.tahaben.core_ui.backgroundDarkHighContrast
import ly.com.tahaben.core_ui.backgroundDarkMediumContrast
import ly.com.tahaben.core_ui.backgroundLight
import ly.com.tahaben.core_ui.backgroundLightClassic
import ly.com.tahaben.core_ui.backgroundLightHighContrast
import ly.com.tahaben.core_ui.backgroundLightMediumContrast
import ly.com.tahaben.core_ui.errorContainerDark
import ly.com.tahaben.core_ui.errorContainerDarkClassic
import ly.com.tahaben.core_ui.errorContainerDarkHighContrast
import ly.com.tahaben.core_ui.errorContainerDarkMediumContrast
import ly.com.tahaben.core_ui.errorContainerLight
import ly.com.tahaben.core_ui.errorContainerLightClassic
import ly.com.tahaben.core_ui.errorContainerLightHighContrast
import ly.com.tahaben.core_ui.errorContainerLightMediumContrast
import ly.com.tahaben.core_ui.errorDark
import ly.com.tahaben.core_ui.errorDarkClassic
import ly.com.tahaben.core_ui.errorDarkHighContrast
import ly.com.tahaben.core_ui.errorDarkMediumContrast
import ly.com.tahaben.core_ui.errorLight
import ly.com.tahaben.core_ui.errorLightClassic
import ly.com.tahaben.core_ui.errorLightHighContrast
import ly.com.tahaben.core_ui.errorLightMediumContrast
import ly.com.tahaben.core_ui.inverseOnSurfaceDark
import ly.com.tahaben.core_ui.inverseOnSurfaceDarkClassic
import ly.com.tahaben.core_ui.inverseOnSurfaceDarkHighContrast
import ly.com.tahaben.core_ui.inverseOnSurfaceDarkMediumContrast
import ly.com.tahaben.core_ui.inverseOnSurfaceLight
import ly.com.tahaben.core_ui.inverseOnSurfaceLightClassic
import ly.com.tahaben.core_ui.inverseOnSurfaceLightHighContrast
import ly.com.tahaben.core_ui.inverseOnSurfaceLightMediumContrast
import ly.com.tahaben.core_ui.inversePrimaryDark
import ly.com.tahaben.core_ui.inversePrimaryDarkClassic
import ly.com.tahaben.core_ui.inversePrimaryDarkHighContrast
import ly.com.tahaben.core_ui.inversePrimaryDarkMediumContrast
import ly.com.tahaben.core_ui.inversePrimaryLight
import ly.com.tahaben.core_ui.inversePrimaryLightClassic
import ly.com.tahaben.core_ui.inversePrimaryLightHighContrast
import ly.com.tahaben.core_ui.inversePrimaryLightMediumContrast
import ly.com.tahaben.core_ui.inverseSurfaceDark
import ly.com.tahaben.core_ui.inverseSurfaceDarkClassic
import ly.com.tahaben.core_ui.inverseSurfaceDarkHighContrast
import ly.com.tahaben.core_ui.inverseSurfaceDarkMediumContrast
import ly.com.tahaben.core_ui.inverseSurfaceLight
import ly.com.tahaben.core_ui.inverseSurfaceLightClassic
import ly.com.tahaben.core_ui.inverseSurfaceLightHighContrast
import ly.com.tahaben.core_ui.inverseSurfaceLightMediumContrast
import ly.com.tahaben.core_ui.onBackgroundDark
import ly.com.tahaben.core_ui.onBackgroundDarkClassic
import ly.com.tahaben.core_ui.onBackgroundDarkHighContrast
import ly.com.tahaben.core_ui.onBackgroundDarkMediumContrast
import ly.com.tahaben.core_ui.onBackgroundLight
import ly.com.tahaben.core_ui.onBackgroundLightClassic
import ly.com.tahaben.core_ui.onBackgroundLightHighContrast
import ly.com.tahaben.core_ui.onBackgroundLightMediumContrast
import ly.com.tahaben.core_ui.onErrorContainerDark
import ly.com.tahaben.core_ui.onErrorContainerDarkClassic
import ly.com.tahaben.core_ui.onErrorContainerDarkHighContrast
import ly.com.tahaben.core_ui.onErrorContainerDarkMediumContrast
import ly.com.tahaben.core_ui.onErrorContainerLight
import ly.com.tahaben.core_ui.onErrorContainerLightClassic
import ly.com.tahaben.core_ui.onErrorContainerLightHighContrast
import ly.com.tahaben.core_ui.onErrorContainerLightMediumContrast
import ly.com.tahaben.core_ui.onErrorDark
import ly.com.tahaben.core_ui.onErrorDarkClassic
import ly.com.tahaben.core_ui.onErrorDarkHighContrast
import ly.com.tahaben.core_ui.onErrorDarkMediumContrast
import ly.com.tahaben.core_ui.onErrorLight
import ly.com.tahaben.core_ui.onErrorLightClassic
import ly.com.tahaben.core_ui.onErrorLightHighContrast
import ly.com.tahaben.core_ui.onErrorLightMediumContrast
import ly.com.tahaben.core_ui.onPrimaryContainerDark
import ly.com.tahaben.core_ui.onPrimaryContainerDarkClassic
import ly.com.tahaben.core_ui.onPrimaryContainerDarkHighContrast
import ly.com.tahaben.core_ui.onPrimaryContainerDarkMediumContrast
import ly.com.tahaben.core_ui.onPrimaryContainerLight
import ly.com.tahaben.core_ui.onPrimaryContainerLightClassic
import ly.com.tahaben.core_ui.onPrimaryContainerLightHighContrast
import ly.com.tahaben.core_ui.onPrimaryContainerLightMediumContrast
import ly.com.tahaben.core_ui.onPrimaryDark
import ly.com.tahaben.core_ui.onPrimaryDarkClassic
import ly.com.tahaben.core_ui.onPrimaryDarkHighContrast
import ly.com.tahaben.core_ui.onPrimaryDarkMediumContrast
import ly.com.tahaben.core_ui.onPrimaryLight
import ly.com.tahaben.core_ui.onPrimaryLightClassic
import ly.com.tahaben.core_ui.onPrimaryLightHighContrast
import ly.com.tahaben.core_ui.onPrimaryLightMediumContrast
import ly.com.tahaben.core_ui.onSecondaryContainerDark
import ly.com.tahaben.core_ui.onSecondaryContainerDarkClassic
import ly.com.tahaben.core_ui.onSecondaryContainerDarkHighContrast
import ly.com.tahaben.core_ui.onSecondaryContainerDarkMediumContrast
import ly.com.tahaben.core_ui.onSecondaryContainerLight
import ly.com.tahaben.core_ui.onSecondaryContainerLightClassic
import ly.com.tahaben.core_ui.onSecondaryContainerLightHighContrast
import ly.com.tahaben.core_ui.onSecondaryContainerLightMediumContrast
import ly.com.tahaben.core_ui.onSecondaryDark
import ly.com.tahaben.core_ui.onSecondaryDarkClassic
import ly.com.tahaben.core_ui.onSecondaryDarkHighContrast
import ly.com.tahaben.core_ui.onSecondaryDarkMediumContrast
import ly.com.tahaben.core_ui.onSecondaryLight
import ly.com.tahaben.core_ui.onSecondaryLightClassic
import ly.com.tahaben.core_ui.onSecondaryLightHighContrast
import ly.com.tahaben.core_ui.onSecondaryLightMediumContrast
import ly.com.tahaben.core_ui.onSurfaceDark
import ly.com.tahaben.core_ui.onSurfaceDarkClassic
import ly.com.tahaben.core_ui.onSurfaceDarkHighContrast
import ly.com.tahaben.core_ui.onSurfaceDarkMediumContrast
import ly.com.tahaben.core_ui.onSurfaceLight
import ly.com.tahaben.core_ui.onSurfaceLightClassic
import ly.com.tahaben.core_ui.onSurfaceLightHighContrast
import ly.com.tahaben.core_ui.onSurfaceLightMediumContrast
import ly.com.tahaben.core_ui.onSurfaceVariantDark
import ly.com.tahaben.core_ui.onSurfaceVariantDarkClassic
import ly.com.tahaben.core_ui.onSurfaceVariantDarkHighContrast
import ly.com.tahaben.core_ui.onSurfaceVariantDarkMediumContrast
import ly.com.tahaben.core_ui.onSurfaceVariantLight
import ly.com.tahaben.core_ui.onSurfaceVariantLightClassic
import ly.com.tahaben.core_ui.onSurfaceVariantLightHighContrast
import ly.com.tahaben.core_ui.onSurfaceVariantLightMediumContrast
import ly.com.tahaben.core_ui.onTertiaryContainerDark
import ly.com.tahaben.core_ui.onTertiaryContainerDarkClassic
import ly.com.tahaben.core_ui.onTertiaryContainerDarkHighContrast
import ly.com.tahaben.core_ui.onTertiaryContainerDarkMediumContrast
import ly.com.tahaben.core_ui.onTertiaryContainerLight
import ly.com.tahaben.core_ui.onTertiaryContainerLightClassic
import ly.com.tahaben.core_ui.onTertiaryContainerLightHighContrast
import ly.com.tahaben.core_ui.onTertiaryContainerLightMediumContrast
import ly.com.tahaben.core_ui.onTertiaryDark
import ly.com.tahaben.core_ui.onTertiaryDarkClassic
import ly.com.tahaben.core_ui.onTertiaryDarkHighContrast
import ly.com.tahaben.core_ui.onTertiaryDarkMediumContrast
import ly.com.tahaben.core_ui.onTertiaryLight
import ly.com.tahaben.core_ui.onTertiaryLightClassic
import ly.com.tahaben.core_ui.onTertiaryLightHighContrast
import ly.com.tahaben.core_ui.onTertiaryLightMediumContrast
import ly.com.tahaben.core_ui.outlineDark
import ly.com.tahaben.core_ui.outlineDarkClassic
import ly.com.tahaben.core_ui.outlineDarkHighContrast
import ly.com.tahaben.core_ui.outlineDarkMediumContrast
import ly.com.tahaben.core_ui.outlineLight
import ly.com.tahaben.core_ui.outlineLightClassic
import ly.com.tahaben.core_ui.outlineLightHighContrast
import ly.com.tahaben.core_ui.outlineLightMediumContrast
import ly.com.tahaben.core_ui.outlineVariantDark
import ly.com.tahaben.core_ui.outlineVariantDarkClassic
import ly.com.tahaben.core_ui.outlineVariantDarkHighContrast
import ly.com.tahaben.core_ui.outlineVariantDarkMediumContrast
import ly.com.tahaben.core_ui.outlineVariantLight
import ly.com.tahaben.core_ui.outlineVariantLightClassic
import ly.com.tahaben.core_ui.outlineVariantLightHighContrast
import ly.com.tahaben.core_ui.outlineVariantLightMediumContrast
import ly.com.tahaben.core_ui.primaryContainerDark
import ly.com.tahaben.core_ui.primaryContainerDarkClassic
import ly.com.tahaben.core_ui.primaryContainerDarkHighContrast
import ly.com.tahaben.core_ui.primaryContainerDarkMediumContrast
import ly.com.tahaben.core_ui.primaryContainerLight
import ly.com.tahaben.core_ui.primaryContainerLightClassic
import ly.com.tahaben.core_ui.primaryContainerLightHighContrast
import ly.com.tahaben.core_ui.primaryContainerLightMediumContrast
import ly.com.tahaben.core_ui.primaryDark
import ly.com.tahaben.core_ui.primaryDarkClassic
import ly.com.tahaben.core_ui.primaryDarkHighContrast
import ly.com.tahaben.core_ui.primaryDarkMediumContrast
import ly.com.tahaben.core_ui.primaryLight
import ly.com.tahaben.core_ui.primaryLightClassic
import ly.com.tahaben.core_ui.primaryLightHighContrast
import ly.com.tahaben.core_ui.primaryLightMediumContrast
import ly.com.tahaben.core_ui.scrimDark
import ly.com.tahaben.core_ui.scrimDarkClassic
import ly.com.tahaben.core_ui.scrimDarkHighContrast
import ly.com.tahaben.core_ui.scrimDarkMediumContrast
import ly.com.tahaben.core_ui.scrimLight
import ly.com.tahaben.core_ui.scrimLightClassic
import ly.com.tahaben.core_ui.scrimLightHighContrast
import ly.com.tahaben.core_ui.scrimLightMediumContrast
import ly.com.tahaben.core_ui.secondaryContainerDark
import ly.com.tahaben.core_ui.secondaryContainerDarkClassic
import ly.com.tahaben.core_ui.secondaryContainerDarkHighContrast
import ly.com.tahaben.core_ui.secondaryContainerDarkMediumContrast
import ly.com.tahaben.core_ui.secondaryContainerLight
import ly.com.tahaben.core_ui.secondaryContainerLightClassic
import ly.com.tahaben.core_ui.secondaryContainerLightHighContrast
import ly.com.tahaben.core_ui.secondaryContainerLightMediumContrast
import ly.com.tahaben.core_ui.secondaryDark
import ly.com.tahaben.core_ui.secondaryDarkClassic
import ly.com.tahaben.core_ui.secondaryDarkHighContrast
import ly.com.tahaben.core_ui.secondaryDarkMediumContrast
import ly.com.tahaben.core_ui.secondaryLight
import ly.com.tahaben.core_ui.secondaryLightClassic
import ly.com.tahaben.core_ui.secondaryLightHighContrast
import ly.com.tahaben.core_ui.secondaryLightMediumContrast
import ly.com.tahaben.core_ui.surfaceDark
import ly.com.tahaben.core_ui.surfaceDarkClassic
import ly.com.tahaben.core_ui.surfaceDarkHighContrast
import ly.com.tahaben.core_ui.surfaceDarkMediumContrast
import ly.com.tahaben.core_ui.surfaceLight
import ly.com.tahaben.core_ui.surfaceLightClassic
import ly.com.tahaben.core_ui.surfaceLightHighContrast
import ly.com.tahaben.core_ui.surfaceLightMediumContrast
import ly.com.tahaben.core_ui.surfaceVariantDark
import ly.com.tahaben.core_ui.surfaceVariantDarkClassic
import ly.com.tahaben.core_ui.surfaceVariantDarkHighContrast
import ly.com.tahaben.core_ui.surfaceVariantDarkMediumContrast
import ly.com.tahaben.core_ui.surfaceVariantLight
import ly.com.tahaben.core_ui.surfaceVariantLightClassic
import ly.com.tahaben.core_ui.surfaceVariantLightHighContrast
import ly.com.tahaben.core_ui.surfaceVariantLightMediumContrast
import ly.com.tahaben.core_ui.tertiaryContainerDark
import ly.com.tahaben.core_ui.tertiaryContainerDarkClassic
import ly.com.tahaben.core_ui.tertiaryContainerDarkHighContrast
import ly.com.tahaben.core_ui.tertiaryContainerDarkMediumContrast
import ly.com.tahaben.core_ui.tertiaryContainerLight
import ly.com.tahaben.core_ui.tertiaryContainerLightClassic
import ly.com.tahaben.core_ui.tertiaryContainerLightHighContrast
import ly.com.tahaben.core_ui.tertiaryContainerLightMediumContrast
import ly.com.tahaben.core_ui.tertiaryDark
import ly.com.tahaben.core_ui.tertiaryDarkClassic
import ly.com.tahaben.core_ui.tertiaryDarkHighContrast
import ly.com.tahaben.core_ui.tertiaryDarkMediumContrast
import ly.com.tahaben.core_ui.tertiaryLight
import ly.com.tahaben.core_ui.tertiaryLightClassic
import ly.com.tahaben.core_ui.tertiaryLightHighContrast
import ly.com.tahaben.core_ui.tertiaryLightMediumContrast
import timber.log.Timber


private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
)

private val classicLightScheme = lightColorScheme(
    primary = primaryLightClassic,
    onPrimary = onPrimaryLightClassic,
    primaryContainer = primaryContainerLightClassic,
    onPrimaryContainer = onPrimaryContainerLightClassic,
    secondary = secondaryLightClassic,
    onSecondary = onSecondaryLightClassic,
    secondaryContainer = secondaryContainerLightClassic,
    onSecondaryContainer = onSecondaryContainerLightClassic,
    tertiary = tertiaryLightClassic,
    onTertiary = onTertiaryLightClassic,
    tertiaryContainer = tertiaryContainerLightClassic,
    onTertiaryContainer = onTertiaryContainerLightClassic,
    error = errorLightClassic,
    onError = onErrorLightClassic,
    errorContainer = errorContainerLightClassic,
    onErrorContainer = onErrorContainerLightClassic,
    background = backgroundLightClassic,
    onBackground = onBackgroundLightClassic,
    surface = surfaceLightClassic,
    onSurface = onSurfaceLightClassic,
    surfaceVariant = surfaceVariantLightClassic,
    onSurfaceVariant = onSurfaceVariantLightClassic,
    outline = outlineLightClassic,
    outlineVariant = outlineVariantLightClassic,
    scrim = scrimLightClassic,
    inverseSurface = inverseSurfaceLightClassic,
    inverseOnSurface = inverseOnSurfaceLightClassic,
    inversePrimary = inversePrimaryLightClassic,
)
private val classicDarkScheme = darkColorScheme(
    primary = primaryDarkClassic,
    onPrimary = onPrimaryDarkClassic,
    primaryContainer = primaryContainerDarkClassic,
    onPrimaryContainer = onPrimaryContainerDarkClassic,
    secondary = secondaryDarkClassic,
    onSecondary = onSecondaryDarkClassic,
    secondaryContainer = secondaryContainerDarkClassic,
    onSecondaryContainer = onSecondaryContainerDarkClassic,
    tertiary = tertiaryDarkClassic,
    onTertiary = onTertiaryDarkClassic,
    tertiaryContainer = tertiaryContainerDarkClassic,
    onTertiaryContainer = onTertiaryContainerDarkClassic,
    error = errorDarkClassic,
    onError = onErrorDarkClassic,
    errorContainer = errorContainerDarkClassic,
    onErrorContainer = onErrorContainerDarkClassic,
    background = backgroundDarkClassic,
    onBackground = onBackgroundDarkClassic,
    surface = surfaceDarkClassic,
    onSurface = onSurfaceDarkClassic,
    surfaceVariant = surfaceVariantDarkClassic,
    onSurfaceVariant = onSurfaceVariantDarkClassic,
    outline = outlineDarkClassic,
    outlineVariant = outlineVariantDarkClassic,
    scrim = scrimDarkClassic,
    inverseSurface = inverseSurfaceDarkClassic,
    inverseOnSurface = inverseOnSurfaceDarkClassic,
    inversePrimary = inversePrimaryDarkClassic,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
)

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
)


@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)

@Composable
fun FarhanTheme(
    darkMode: Boolean,
    // Dynamic color is available on Android 12+
    colorStyle: ThemeColors,
    content: @Composable() () -> Unit
) {
    Timber.d("theme called: color style = $colorStyle")
    val colorScheme = when (colorStyle) {
        ThemeColors.Dynamic -> {
            val context = LocalContext.current
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (darkMode) dynamicDarkColorScheme(context) else dynamicLightColorScheme(
                    context
                )
            } else {
                throw Exception("Dynamic option is only available on Android 12+")
            }
        }

        ThemeColors.Classic -> {
            if (darkMode) classicDarkScheme else classicLightScheme
        }

        ThemeColors.Vibrant -> {
            if (darkMode) darkScheme else lightScheme
        }
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkMode
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

