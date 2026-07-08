package ly.com.tahaben.core_ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ly.com.tahaben.core.model.ThemeColors
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.theme.FarhanTheme
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
fun DelayedUnlockOverlay(
    modifier: Modifier = Modifier,
    delayInSeconds: Int,
    delayMessage: String,
    onCountdownFinished: () -> Unit
) {
    val animDuration = delayInSeconds.seconds.inWholeMilliseconds * 0.25
    val animatedBoxHeight = remember { Animatable(0f) }
    val config = LocalConfiguration.current
    var isMessageVisible by remember { mutableStateOf(false) }
    val spacing = LocalSpacing.current
    LaunchedEffect(true) {
        isMessageVisible = true
        animatedBoxHeight.animateTo(
            config.screenHeightDp.toFloat(), animationSpec = tween(
                animDuration.toInt()
            )
        )
        delay(animDuration.milliseconds)
        delay(animDuration.milliseconds)
        animatedBoxHeight.animateTo(0f, animationSpec = tween(animDuration.toInt()))
        isMessageVisible = false
        onCountdownFinished()
    }
    Box(
        modifier
            .fillMaxSize()
            .pointerInput(key1 = null) {} // to prevent taps underneath the box
            .background(MaterialTheme.colorScheme.background),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(animatedBoxHeight.value.dp)
                .background(MaterialTheme.colorScheme.surfaceTint),
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            AnimatedVisibility(isMessageVisible, enter = fadeIn(), exit = fadeOut()) {
                Text(
                    delayMessage,
                    modifier = Modifier.padding(horizontal = spacing.spaceMedium),
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewDelayedUnlockOverlay() {
    FarhanTheme(false, ThemeColors.Classic) {
        DelayedUnlockOverlay(
            delayInSeconds = 4,
            delayMessage = "Take a deep breath!",
            onCountdownFinished = {})
    }
}
