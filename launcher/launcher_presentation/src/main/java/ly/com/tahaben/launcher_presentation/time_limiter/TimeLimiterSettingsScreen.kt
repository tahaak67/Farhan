package ly.com.tahaben.launcher_presentation.time_limiter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.OnLifecycleEvent
import ly.com.tahaben.core_ui.components.HowDialog
import ly.com.tahaben.core_ui.components.PermissionNotGrantedContent
import ly.com.tahaben.core_ui.mirror

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 18,Feb,2023
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeLimiterSettingsScreen(
    onNavigateUp: () -> Unit,
    onNavigateToWhitelist: () -> Unit,
    viewModel: TimeLimiterSettingsViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current
    val state = viewModel.state.collectAsState().value
    val openHowDialog = remember { mutableStateOf(false) }
    //val openHowAccessibilityDialog = remember { mutableStateOf(false) }


    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.checkTimeLimiterStats()
            }
            else -> Unit
        }
    }

    Column {
        TopAppBar(
            title = {
                Text(text = stringResource(R.string.app_time_limiter_settings))
            },
//            backgroundColor = White,
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        modifier = Modifier.mirror(),
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
            }
        )
        if (!state.isAppearOnTopPermissionGranted) {
            PermissionNotGrantedContent(
                modifier = Modifier.fillMaxSize(),
                message = stringResource(R.string.appear_on_top_permission_not_granted),
                subMessage = "Farhan needs appear on top permission to display a warning on top of other apps when your time limit is up.",
                onGrantClick = viewModel::askForAppearOnTopPermission,
                onHowClick = { openHowDialog.value = true }
            )

        } /*else if (!state.isAccessibilityPermissionGranted) {
            val messages = listOf(
                "Know what app is currently running."
            )
            val permissionReasons =
                getAnnotatedStringBulletList(messages)

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.one_more_thing),
                    style = MaterialTheme.typography.h3,
                    textAlign = TextAlign.Center
                )
                AccessibilityNotRunningContent(
                    modifier = Modifier,
                    message = stringResource(R.string.accessibility_permission_not_granted),
                    subMessage = stringResource(R.string.accessibility_permission_needed_message),
                    permissionReasons = permissionReasons,
                    onGrantClick = viewModel::askForAccessibilityPermission,
                    onBack = onNavigateUp,
                    onHowClick = { openHowAccessibilityDialog.value = true }
                )
            }
        }*/ else {
            Column(
                Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceMedium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,

                    ) {
                    Text(text = stringResource(id = R.string.app_time_limiter))
                    Switch(
                        checked = state.isTimeLimiterEnabled,
                        onCheckedChange = { checked ->
                            viewModel.setTimeLimiterEnabled(checked)
                            //isServiceChecked.value = checked
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceMedium)
                        .clickable {
                            onNavigateToWhitelist()
                        },
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.white_lited_apps),
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }


    if (openHowDialog.value) {
        HowDialog(
            gifId = R.drawable.appear_on_top_permission_howto,
            gifDescription = stringResource(R.string.how_to_enable_permission),
            onDismiss = { openHowDialog.value = false }
        )
    }
    /*if (openHowAccessibilityDialog.value) {
        HowDialog(
            gifId = R.drawable.accessibility_permission_howto,
            gifDescription = stringResource(R.string.how_to_enable_permission),
            onDismiss = { openHowAccessibilityDialog.value = false }
        )
    }*/
}