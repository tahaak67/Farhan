package ly.com.tahaben.usage_overview_presentation.settings

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import ly.com.tahaben.core.R
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.core_ui.DarkerYellow
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.Page
import ly.com.tahaben.core_ui.components.PermissionDialog
import ly.com.tahaben.core_ui.components.PostNotificationPermissionTextProvider
import ly.com.tahaben.core_ui.mirror
import timber.log.Timber

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 06,May,2023
 */
@Composable
fun UsageSettingsScreen(
    onNavigateUp: () -> Unit,
    viewModel: UsageSettingsViewModel = hiltViewModel(),
    shouldShowRational: (String) -> Boolean,
    scaffoldState: ScaffoldState
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
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = uiEvent.message.asString(
                            context
                        )
                    )
                }

                is UiEvent.HideSnackBar -> {
                    Timber.d("dismiss snackbar event here")
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                }

                else -> Unit
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text(text = stringResource(id = R.string.usage_settings)) },
            backgroundColor = Color.White,
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,

            ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.cache_usage_data), fontWeight = FontWeight.Bold)
                Text(text = stringResource(R.string.cache_usage_data_description))
            }

            Switch(
                checked = state.isCacheEnabled,
                onCheckedChange = { checked ->
                    viewModel.setCachingEnabled(checked)
                }
            )
        }
        AnimatedVisibility(visible = state.isCacheEnabled) {
            Column(modifier = Modifier) {
                Row(
                    modifier = Modifier
                        .clickable {
                            viewModel.setAutoCachingEnabled(!state.isAutoCachingEnabled)
                        }
                        .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceMedium)
                        .fillMaxWidth(),
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
            }
        }
        AnimatedVisibility(visible = state.isAutoCachingEnabled) {
            Row(
                modifier = Modifier
                    .clickable {
                        viewModel.onEvent(UsageSettingsEvent.ShowSelectReportsDialog)
                    }
                    .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceMedium)
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

            Card(
                modifier = Modifier,
                shape = RoundedCornerShape(spacing.spaceMedium)
            ) {
                Column(
                    modifier = Modifier
                        .background(Page)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(spacing.spaceMedium)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = stringResource(id = R.string.weekly))
                        Checkbox(
                            colors = CheckboxDefaults.colors(checkedColor = DarkerYellow),
                            checked = state.isWeeklyReportsEnabled,
                            onCheckedChange = { isChecked ->
                                viewModel.setWeeklyReportsEnabled(isChecked)
                            })
                    }
                    Row(
                        modifier = Modifier
                            .padding(spacing.spaceMedium)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = stringResource(id = R.string.monthly))
                        Checkbox(
                            colors = CheckboxDefaults.colors(checkedColor = DarkerYellow),
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
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = stringResource(id = R.string.yearly))
                            Checkbox(
                                colors = CheckboxDefaults.colors(checkedColor = DarkerYellow),
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