package ly.com.tahaben.screen_grayscale_presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ly.com.tahaben.core.R
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.OnLifecycleEvent
import ly.com.tahaben.core_ui.components.AccessibilityNotRunningContent
import ly.com.tahaben.core_ui.components.HowDialog
import ly.com.tahaben.core_ui.components.getAnnotatedStringBulletList
import ly.com.tahaben.core_ui.mirror

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrayscaleScreen(
    onNavigateUp: () -> Unit,
    onNavigateToExceptions: () -> Unit,
    viewModel: GrayscaleViewModel = hiltViewModel(),
    scaffoldState: SnackbarHostState
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val isServiceChecked = remember { mutableStateOf(state.isServiceEnabled) }
    isServiceChecked.value = state.isServiceEnabled
    val openHowAccessibilityDialog = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    scaffoldState.showSnackbar(event.message.asString(context))
                }

                else -> Unit
            }
        }
    }

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
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
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
                    .padding(horizontal = spacing.spaceMedium)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(
                        text = stringResource(R.string.secure_settings_permission_msg),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(spacing.spaceMedium))
                    Text(
                        text = stringResource(R.string.secure_settings_permission_sub_msg),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(spacing.spaceMedium))
                    if (state.isDeviceRooted) {
                        Text(
                            text = stringResource(R.string.device_root_detected_msg),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(spacing.spaceMedium))
                        Button(onClick = { viewModel.askForSecureSettingsPermissionWithRoot() }) {
                            Text(
                                text = stringResource(id = R.string.grant_access),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(spacing.spaceMedium))
                        Text(
                            text = stringResource(R.string.grant_secure_permission_manually_msg),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.no_device_root_detected_msg),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
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
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                    Spacer(modifier = Modifier.height(spacing.spaceMedium))

                }
            }
        } else if (!state.isAccessibilityPermissionGranted) {
            val messages = listOf(
                stringResource(R.string.reason_know_if_app_in_whitelist),
            )
            val permissionReasons =
                getAnnotatedStringBulletList(messages)

            AccessibilityNotRunningContent(
                modifier = Modifier.fillMaxSize(),
                message = stringResource(id = R.string.accessibility_permission_not_granted),
                subMessage = stringResource(id = R.string.accessibility_permission_needed_grayscale_message),
                permissionReasons = permissionReasons,
                onHowClick = { openHowAccessibilityDialog.value = true },
                onGrantClick = viewModel::askForAccessibilityPermission,
                onBack = onNavigateUp
            )
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.spaceMedium)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing.spaceMedium)
                        .selectable(
                            selected = state.isServiceEnabled,
                            onClick = {
                                viewModel.setServiceStats(!state.isServiceEnabled)
                            },
                            role = Role.Switch
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,

                    ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.enable_grayscale_for_white_listed_apps)
                    )
                    Switch(
                        checked = state.isServiceEnabled,
                        onCheckedChange = { checked ->
                            viewModel.setServiceStats(checked)
                        }
                    )
                }
                Divider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing.spaceMedium)
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
                Divider()
            }
        }
    }
    if (openHowAccessibilityDialog.value) {
        HowDialog(
            gifId = R.drawable.accessibility_permission_howto,
            gifDescription = stringResource(R.string.how_to_enable_permission),
            onDismiss = { openHowAccessibilityDialog.value = false }
        )
    }
}


