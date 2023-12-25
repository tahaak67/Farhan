package ly.com.tahaben.onboarding_presentaion.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import ly.com.tahaben.domain.model.UIModeAppearance

@Composable
fun UIModeAppearance.isCurrentlyDark(): Boolean {
    return when (this) {
        UIModeAppearance.DARK_MODE -> true
        UIModeAppearance.LIGHT_MODE -> false
        UIModeAppearance.FOLLOW_SYSTEM -> isSystemInDarkTheme()
    }
}