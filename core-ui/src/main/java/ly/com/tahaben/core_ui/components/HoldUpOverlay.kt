package ly.com.tahaben.core_ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun DelayedLaunchOverlay(modifier: Modifier = Modifier, isDelayRunning: Boolean, openApp: () -> Unit, dismissOverlay: () -> Unit) {
    val animDuration = 1000
    val animatedBoxHeight = remember { androidx.compose.animation.core.Animatable(0f) }
    val config = LocalConfiguration.current
    var isFirstHeadlineVisible by remember { mutableStateOf(false) }
    val isConfirmOptionsVisible = remember { derivedStateOf {
        animatedBoxHeight.value == 0f && !animatedBoxHeight.isRunning
    } }
    val scope = rememberCoroutineScope()
    LaunchedEffect(isDelayRunning) {
        if (isDelayRunning) {
            isFirstHeadlineVisible = true
            animatedBoxHeight.animateTo(config.screenHeightDp.toFloat(), animationSpec = tween(animDuration))
            delay(animDuration.milliseconds)
            isFirstHeadlineVisible = false
            delay(animDuration.milliseconds)
            animatedBoxHeight.animateTo(0f, animationSpec = tween(animDuration))
        }
    }
    if (isDelayRunning) {

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
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(isFirstHeadlineVisible, enter = fadeIn(), exit = fadeOut()){
                    Text("Take a deep breath!", fontSize = 35.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
                AnimatedVisibility(isConfirmOptionsVisible.value, enter = fadeIn(), exit = fadeOut()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        TextButton(onClick = {
                            openApp()
                        }) {
                            Text("Continue to app", fontSize = 24.sp)
                        }
                        Button(onClick = {
                            dismissOverlay()
                            scope.launch { animatedBoxHeight.snapTo(0f) }
                            //isConfirmOptionsVisible = false
                        }) {
                            Text("Leave", fontSize = 24.sp)
                        }
                    }
                }
            }
        }

    }
}