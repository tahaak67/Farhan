package ly.com.tahaben.core_ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ly.com.tahaben.core.R
import ly.com.tahaben.core.model.ThemeColors
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.theme.FarhanTheme
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
fun DelayedLaunchOverlay(
    modifier: Modifier = Modifier,
    delayInSeconds: Int,
    launchCount: Int,
    appName: String,
    delayMessage: String,
    openApp: () -> Unit,
    dismissOverlay: () -> Unit
) {
    val animDuration = delayInSeconds.seconds.inWholeMilliseconds * 0.25
    val animatedBoxHeight = remember { androidx.compose.animation.core.Animatable(0f) }
    val config = LocalConfiguration.current
    var isFirstHeadlineVisible by remember { mutableStateOf(false) }
    val isConfirmOptionsVisible = remember {
        derivedStateOf {
            animatedBoxHeight.value == 0f && !animatedBoxHeight.isRunning
        }
    }
    var launchButtonsAlpha by remember { mutableStateOf(0f) }
    val animLunchButtonsAlpha by animateFloatAsState(launchButtonsAlpha, animationSpec = tween(800))
    val spacing = LocalSpacing.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(true) {
        launchButtonsAlpha = 0f
        isFirstHeadlineVisible = true
        animatedBoxHeight.animateTo(
            config.screenHeightDp.toFloat(), animationSpec = tween(
                animDuration.toInt()
            )
        )
        delay(animDuration.milliseconds)
        delay(animDuration.milliseconds)
        launch {
            animatedBoxHeight.animateTo(0f, animationSpec = tween(animDuration.toInt()))
        }
        isFirstHeadlineVisible = false
        delay(500)
        launchButtonsAlpha = 1f
    }
    Box(
        Modifier
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
            AnimatedVisibility(isFirstHeadlineVisible, enter = fadeIn(), exit = fadeOut()) {
                Text(
                    delayMessage,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            AnimatedVisibility(isConfirmOptionsVisible.value, enter = fadeIn(), exit = fadeOut()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        stringResource(
                            R.string.you_attempted_to_launch_this_app_times,
                            appName,
                            launchCount
                        ),
                        modifier = Modifier.padding(horizontal = spacing.spaceMedium),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(
                        modifier = Modifier.alpha(animLunchButtonsAlpha),
                        onClick = {
                        openApp()
                    }) {
                        Text(stringResource(R.string.continue_to_app), fontSize = 24.sp)
                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.spaceLarge)
                            .alpha(animLunchButtonsAlpha),
                        onClick = {
                            dismissOverlay()
                            scope.launch { animatedBoxHeight.snapTo(0f) }
                            //isConfirmOptionsVisible = false
                        }) {
                        Text(stringResource(R.string.leave), fontSize = 24.sp)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewDelayedLaunchOverlay() {
    FarhanTheme(false, ThemeColors.Classic) {
        DelayedLaunchOverlay(
            delayInSeconds = 4,
            launchCount = 5,
            appName = "Facebook",
            delayMessage = "Take a deep breath!",
            openApp = {},
            dismissOverlay = {})
    }
}