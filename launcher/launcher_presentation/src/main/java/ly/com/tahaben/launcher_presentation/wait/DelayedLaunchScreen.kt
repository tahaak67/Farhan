package ly.com.tahaben.launcher_presentation.wait

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import ly.com.tahaben.core.R
import ly.com.tahaben.core.model.ThemeColors
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.OnLifecycleEvent
import ly.com.tahaben.core_ui.components.AccessibilityNotRunningContent
import ly.com.tahaben.core_ui.components.CommonAppBar
import ly.com.tahaben.core_ui.components.HowDialog
import ly.com.tahaben.core_ui.components.SwitchRow
import ly.com.tahaben.core_ui.components.getAnnotatedStringBulletList
import ly.com.tahaben.core_ui.navigation.openAccessibilitySettings
import ly.com.tahaben.core_ui.theme.FarhanTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DelayedLaunchScreen(
    onNavigateUp: () -> Unit,
    onNavigateToWhitelist: () -> Unit,
    onEvent: (MindfulLaunchEvent) -> Unit,
    state: DelayedLaunchState
) {
    val spacing = LocalSpacing.current
    var openHowAccessibilityDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                onEvent(MindfulLaunchEvent.ScreenShown)
            }

            else -> Unit
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CommonAppBar(
                title = stringResource(R.string.delayed_launch),
                onNavigateUp = {
                    onNavigateUp()
                }
            )
        }
    ) { paddingValues ->
        Column(Modifier
            .padding(paddingValues)
            .fillMaxSize()) {

            if (state.isAccessibilityPermissionGranted){
                SwitchRow(
                    Modifier
                        .fillMaxWidth(),
                    stringResource(R.string.delayed_launch),
                    selected = state.isMindfulLaunchEnabled,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    onEvent(MindfulLaunchEvent.OnMindfulLaunchEnabled(it))
                }
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
//                        .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceMedium)
                        .clickable {
                            onNavigateToWhitelist()
                        },
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.white_lited_apps),
                            textAlign = TextAlign.Start
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background)
                )

            } else {
// accessibility not running
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    val messages = listOf(
                        stringResource(R.string.reason_know_what_app_launched)
                    )
                    val permissionReasons =
                        getAnnotatedStringBulletList(messages)

                    AccessibilityNotRunningContent(
                        modifier = Modifier,
                        message = stringResource(R.string.accessibility_permission_not_granted),
                        subMessage = stringResource(R.string.accessibility_permission_needed_message),
                        permissionReasons = permissionReasons,
                        onGrantClick = { openAccessibilitySettings(context) },
                        onBack = onNavigateUp,
                        onHowClick = { openHowAccessibilityDialog = true }
                    )
                }

            }
        }

    }
    if (openHowAccessibilityDialog) {
        HowDialog(
            gifId = R.drawable.accessibility_permission_howto,
            gifDescription = stringResource(R.string.how_to_enable_permission),
            onDismiss = { openHowAccessibilityDialog = false }
        )
    }
}

@Preview()
@Preview("ar", locale = "ar")
@Composable
private fun MindfulLaunchScreenPreview() {

    FarhanTheme(false, ThemeColors.Classic) {
        DelayedLaunchScreen(
            onNavigateUp = {},
            onNavigateToWhitelist = {},
            onEvent = {},
            state = DelayedLaunchState()
        )
    }
}