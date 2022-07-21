package ly.com.tahaben.screen_grayscale_presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.*
import ly.com.tahaben.core_ui.components.PermissionNotGrantedContent

@Composable
fun GrayscaleScreen(
    onNavigateUp: () -> Unit,
    onNavigateToExceptions: () -> Unit,
    viewModel: GrayscaleViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val state = viewModel.state
    val isServiceChecked = remember { mutableStateOf(state.isServiceEnabled) }
    isServiceChecked.value = state.isServiceEnabled

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.checkServiceStats()
            }
            else -> Unit
        }
    }

    Column {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.grayscale))

            },
            backgroundColor = White,
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
        if (!state.isSecureSettingsPermissionGranted) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.spaceMedium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(
                        text = stringResource(R.string.secure_settings_permission_msg),
                        style = MaterialTheme.typography.h1,
                        color = Black
                    )
                    Spacer(modifier = Modifier.height(spacing.spaceMedium))
                    Text(
                        text = stringResource(R.string.secure_settings_permission_sub_msg),
                        style = MaterialTheme.typography.h3,
                        color = Black
                    )
                    Spacer(modifier = Modifier.height(spacing.spaceMedium))
                    if (state.isDeviceRooted) {
                        Text(
                            text = stringResource(R.string.device_root_detected_msg),
                            style = MaterialTheme.typography.h3,
                            color = Black
                        )
                        Spacer(modifier = Modifier.height(spacing.spaceMedium))
                        Button(onClick = { viewModel.askForSecureSettingsPermissionWithRoot() }) {
                            Text(
                                text = stringResource(id = R.string.grant_access),
                                style = MaterialTheme.typography.button,
                                color = Black
                            )
                        }
                        Spacer(modifier = Modifier.height(spacing.spaceMedium))
                    } else {
                        Text(
                            text = stringResource(R.string.no_device_root_detected_msg),
                            style = MaterialTheme.typography.h3,
                            color = Black
                        )
                        Spacer(modifier = Modifier.height(spacing.spaceMedium))
                        Button(onClick = {
                            val url = Intent(Intent.ACTION_VIEW).apply {
                                data =
                                    Uri.parse("https://tahaben.com.ly/grant-secure-settings-permission-to-farhan/")
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(url)
                        }) {
                            Text(
                                text = stringResource(id = R.string.how),
                                style = MaterialTheme.typography.button,
                                color = Black
                            )
                        }
                        Spacer(modifier = Modifier.height(spacing.spaceMedium))
                    }
                }
            }
        } else if (!state.isAccessibilityPermissionGranted) {
            PermissionNotGrantedContent(
                modifier = Modifier.fillMaxSize(),
                message = stringResource(id = R.string.accessibility_permission_not_granted),
                subMessage = stringResource(id = R.string.accessibility_permission_needed_grayscale_message),
                onGrantClick = viewModel::askForAccessibilityPermission
            ) {

            }
        } else {
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
                    Text(text = stringResource(R.string.enable_grayscale_for_white_listed_apps))
                    Switch(
                        checked = isServiceChecked.value,
                        onCheckedChange = { checked ->
                            viewModel.setServiceStats(checked)
                            isServiceChecked.value = checked
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceMedium)
                        .clickable {
                            onNavigateToExceptions()
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
}


