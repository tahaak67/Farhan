package ly.com.tahaben.usage_overview_presentation.settings

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import ly.com.tahaben.core.R
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.components.PermissionDialog
import ly.com.tahaben.core_ui.components.PostNotificationPermissionTextProvider
import ly.com.tahaben.core_ui.components.SwitchRow
import ly.com.tahaben.core_ui.mirror
import timber.log.Timber

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 06,May,2023
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsageSettingsScreen(
    onNavigateUp: () -> Unit,
    viewModel: UsageSettingsViewModel = hiltViewModel(),
    shouldShowRational: (String) -> Boolean,
    scaffoldState: SnackbarHostState
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val state = viewModel.state
    val dialogQueue = state.visiblePermissionDialogQueue

    val postNotificationPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                viewModel.onPermissionResult(
                    permission = Manifest.permission.POST_NOTIFICATIONS,
                    isGranted = isGranted
                )
            }
        }
    )

    LaunchedEffect(key1 = true) {
        viewModel.event.collect { event ->
            when (event) {
                is UiEventUsageSettings.AutoCacheEnabled -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        postNotificationPermissionResultLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    }
                }
            }
        }
    }
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { uiEvent ->
            when (uiEvent) {
                is UiEvent.ShowSnackbar -> {
                    Timber.d("show snackbar event here")
                    scaffoldState.showSnackbar(
                        message = uiEvent.message.asString(
                            context
                        )
                    )
                }

                is UiEvent.HideSnackBar -> {
                    Timber.d("dismiss snackbar event here")
                    scaffoldState.currentSnackbarData?.dismiss()
                }

                else -> Unit
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            title = { Text(text = stringResource(id = R.string.usage_settings)) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.spaceMedium)
                .selectableGroup(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing.spaceMedium)
                    .selectable(
                        selected = state.isCacheEnabled,
                        onClick = {
                            viewModel.setCachingEnabled(!state.isCacheEnabled)
                        },
                        role = Role.Switch
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,

                ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.cache_usage_data),
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = stringResource(R.string.cache_usage_data_description))
                }

                Switch(
                    checked = state.isCacheEnabled,
                    onCheckedChange = { checked ->
                        viewModel.setCachingEnabled(checked)
                    }
                )
            }
            HorizontalDivider()
            SwitchRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing.spaceMedium),
                string = stringResource(id = R.string.ignore_launcher_usage),
                selected = state.isIgnoreLauncher,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                viewModel.onEvent(UsageSettingsEvent.OnIgnoreLauncherClick(it))
            }
            HorizontalDivider()
            SwitchRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing.spaceMedium),
                string = stringResource(id = R.string.ignore_farhan_usage),
                selected = state.isIgnoreFarhan,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                viewModel.onEvent(UsageSettingsEvent.OnIgnoreFarhanClick(it))
            }
            HorizontalDivider()
            AnimatedVisibility(visible = state.isCacheEnabled) {
                Column(modifier = Modifier) {
                    Row(
                        modifier = Modifier
                            .padding(vertical = spacing.spaceMedium)
                            .fillMaxWidth()
                            .selectable(
                                selected = state.isAutoCachingEnabled,
                                onClick = { viewModel.setAutoCachingEnabled(!state.isAutoCachingEnabled) },
                                role = Role.Switch
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,

                        ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.auto_cache_usage_data),
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = stringResource(R.string.auto_cache_usage_data_description))
                        }
                        Switch(
                            checked = state.isAutoCachingEnabled,
                            onCheckedChange = { checked ->
                                viewModel.setAutoCachingEnabled(checked)
                            }
                        )
                    }
                    HorizontalDivider()
                }
            }
            AnimatedVisibility(visible = state.isAutoCachingEnabled) {
                Row(
                    modifier = Modifier
                        .clickable {
                            viewModel.onEvent(UsageSettingsEvent.ShowSelectReportsDialog)
                        }
                        .padding(vertical = spacing.spaceMedium)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,

                    ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.periodic_usage_reports),
                            fontWeight = FontWeight.Bold
                        )

                        Text(text = buildAnnotatedString {
                            if (!state.isMonthlyReportsEnabled && !state.isWeeklyReportsEnabled && !state.isYearlyReportsEnabled) {
                                append(stringResource(id = R.string.usage_reports_off))
                            } else {
                                append(stringResource(id = R.string.enabled))
                                append(": ")
                                if (state.isWeeklyReportsEnabled) {
                                    append(stringResource(R.string.weekly))
                                    append(" ")
                                }
                                if (state.isMonthlyReportsEnabled) {
                                    append(stringResource(R.string.monthly))
                                    append(" ")
                                }
                                if (state.isYearlyReportsEnabled) {
                                    append(stringResource(R.string.yearly))
                                }
                            }
                        }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
    dialogQueue
        .reversed()
        .forEach { permission ->
            PermissionDialog(
                permissionTextProvider = PostNotificationPermissionTextProvider(),
                isPermanentlyDeclined = !shouldShowRational(permission),
                onDismiss = { viewModel.onEvent(UsageSettingsEvent.DismissPermissionDialog) },
                onOkClick = {
                    viewModel.onEvent(UsageSettingsEvent.DismissPermissionDialog)
                    postNotificationPermissionResultLauncher.launch(
                        permission
                    )
                },
                onGoToAppSettingsClick = {
                    viewModel.openAppSettings()
                    viewModel.onEvent(UsageSettingsEvent.DismissPermissionDialog)
                }
            )
        }
    if (state.showSelectReportsDialog) {
        Dialog(onDismissRequest = { viewModel.onEvent(UsageSettingsEvent.DismissSelectReportsDialog) }) {
            Surface(
                modifier = Modifier,
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(
                    modifier = Modifier
                        .selectableGroup()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(spacing.spaceMedium)
                            .fillMaxWidth()
                            .selectable(
                                selected = state.isWeeklyReportsEnabled,
                                onClick = {
                                    viewModel.setWeeklyReportsEnabled(!state.isWeeklyReportsEnabled)
                                },
                                role = Role.Checkbox
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(id = R.string.weekly),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Checkbox(
                            checked = state.isWeeklyReportsEnabled,
                            onCheckedChange = { isChecked ->
                                viewModel.setWeeklyReportsEnabled(isChecked)
                            })
                    }
                    Row(
                        modifier = Modifier
                            .padding(spacing.spaceMedium)
                            .fillMaxWidth()
                            .selectable(
                                selected = state.isMonthlyReportsEnabled,
                                onClick = {
                                    viewModel.setMonthlyReportsEnabled(!state.isMonthlyReportsEnabled)
                                },
                                role = Role.Checkbox
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = stringResource(id = R.string.monthly))
                        Checkbox(
                            checked = state.isMonthlyReportsEnabled,
                            onCheckedChange = { isChecked ->
                                viewModel.setMonthlyReportsEnabled(isChecked)
                            })
                    }
                    // hide yearly option for now
                    if (false) {
                        Row(
                            modifier = Modifier
                                .padding(spacing.spaceMedium)
                                .fillMaxWidth()
                                .selectable(
                                    selected = state.isYearlyReportsEnabled,
                                    onClick = {
                                        viewModel.setYearlyReportsEnabled(!state.isYearlyReportsEnabled)
                                    },
                                    role = Role.Checkbox
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = stringResource(id = R.string.yearly))
                            Checkbox(
                                checked = state.isYearlyReportsEnabled,
                                onCheckedChange = { isChecked ->
                                    viewModel.setYearlyReportsEnabled(isChecked)
                                })
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            viewModel.onEvent(UsageSettingsEvent.DismissSelectReportsDialog)
                        }) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                        Spacer(modifier = Modifier.width(spacing.spaceSmall))
                        TextButton(onClick = {
                            viewModel.saveUsageReportsEnabled()
                            viewModel.onEvent(UsageSettingsEvent.DismissSelectReportsDialog)
                        }) {
                            Text(text = stringResource(id = R.string.ok))
                        }
                    }
                }
            }
        }
    }
}