package ly.com.tahaben.onboarding_presentaion.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import ly.com.tahaben.core.R
import ly.com.tahaben.core.navigation.Routes
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.core.util.UiText
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.util.isCurrentlyDark
import ly.com.tahaben.onboarding_presentaion.components.MainScreenCard
import ly.com.tahaben.onboarding_presentaion.components.ThemeColorsDialog
import ly.com.tahaben.onboarding_presentaion.components.UiModeDialog
import ly.com.tahaben.showcase_layout_compose.model.Arrow
import ly.com.tahaben.showcase_layout_compose.model.ShowcaseMsg
import ly.com.tahaben.showcase_layout_compose.ui.ShowcaseLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    tip: String,
    isGrayscaleEnabled: Boolean,
    isInfiniteScrollBlockerEnabled: Boolean,
    isNotificationFilterEnabled: Boolean,
    isLauncherEnabled: Boolean,
    isDelayedLaunchEnabled: Boolean,
    navController: NavHostController,
    onEvent: (MainScreenEvent) -> Unit,
    state: MainScreenState,
    snackbarHostState: SnackbarHostState,
    uiEvent: UiEvent
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var mDisplayMenu by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = true) {
        onEvent(MainScreenEvent.OnScreenLaunched)
    }
    LaunchedEffect(key1 = uiEvent) {
        when (uiEvent) {
            UiEvent.HideSnackBar -> Unit
            is UiEvent.ShowSnackbar -> {
                snackbarHostState.showSnackbar(uiEvent.message.asString(context = context))
                onEvent(MainScreenEvent.HideSnackBar)
            }

            else -> Unit
        }
    }

    ShowcaseLayout(
        isShowcasing = state.shouldShowcaseAppearanceMenu == true,
        onFinish = { onEvent(MainScreenEvent.AppearanceShowcaseFinished) },
        initIndex = 1,
        isDarkLayout = state.uiMode.isCurrentlyDark(),
        animationDuration = 500
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()

        ) {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                actions = {

                    // Creating Icon button for dropdown menu
                    IconButton(
                        modifier = Modifier.showcase(
                            1,
                            ShowcaseMsg(
                                "Dark mode and theme colors can be changed from the drop down menu.",
                                textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onPrimaryContainer),
                                msgBackground = MaterialTheme.colorScheme.primaryContainer,
                                roundedCorner = 15.dp,
                                arrow = Arrow(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    curved = true
                                )
                            )
                        ),
                        onClick = { mDisplayMenu = !mDisplayMenu }) {
                        Icon(Icons.Default.MoreVert, stringResource(R.string.drop_down_menu))
                    }

                    // Creating a dropdown menu
                    DropdownMenu(
                        expanded = mDisplayMenu,
                        onDismissRequest = { mDisplayMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.about_app),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                navController.navigate(Routes.ABOUT_APP)
                            })
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.appearance),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }, onClick = {
                                onEvent(MainScreenEvent.ShowUiAppearanceDialog)
                                mDisplayMenu = false
                            })
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(id = R.string.theme_colors),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }, onClick = {
                                onEvent(MainScreenEvent.ShowThemeColorsDialog)
                                mDisplayMenu = false
                            })
                    }
                }
            )
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = spacing.spaceExtraLarge)
            ) {
                Spacer(modifier = Modifier.height(spacing.spaceSmall))
                Text(
                    text = stringResource(id = R.string.hello),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_tip),
                        contentDescription = stringResource(R.string.tip_icon_description),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary)
                    )
                    Spacer(modifier = Modifier.width(spacing.spaceSmall))
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(vertical = spacing.spaceMedium),
                    verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onEvent(MainScreenEvent.SaveMainSwitchState(!state.isMainSwitchEnabled))
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Spacer(modifier = Modifier.height(spacing.spaceSmall))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = spacing.spaceMedium),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(
                                    text = if (state.isMainSwitchEnabled) stringResource(R.string.main_switch_is_on) else stringResource(
                                        R.string.main_switch_is_off
                                    ),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = if (state.isMainSwitchEnabled) stringResource(R.string.tap_to_pause_all_farhan_features) else stringResource(
                                        R.string.tap_to_resume_all_farhan_features
                                    ),
                                    style = MaterialTheme.typography.headlineMedium,
                                    lineHeight = 20.sp
                                )
                            }
                            Switch(
                                checked = state.isMainSwitchEnabled,
                                onCheckedChange = { isChecked ->
                                    onEvent(MainScreenEvent.SaveMainSwitchState(isChecked))
                                },
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = MaterialTheme.colorScheme.tertiary,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.tertiaryContainer
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(spacing.spaceSmall))
                    }

                    MainScreenCard(
                        text = stringResource(R.string.usage),
                        status = "",
                        iconId = R.drawable.ic_usage,
                        onClick = { navController.navigate(Routes.USAGE) },
                        mainSwitchEnabled = true,
                        showSnackBar = {
                            onEvent(
                                MainScreenEvent.ShowSnackBar(
                                    UiText.StringResource(
                                        R.string.please_turn_on_main_switch
                                    )
                                )
                            )
                        }
                    )
                    MainScreenCard(
                        text = stringResource(R.string.notifications_filter),
                        status = if (isNotificationFilterEnabled) stringResource(id = R.string.enabled) else stringResource(
                            id = R.string.disabled
                        ),
                        iconId = R.drawable.ic_notification,
                        onClick = { navController.navigate(Routes.NOTIFICATION_FILTER) },
                        mainSwitchEnabled = state.isMainSwitchEnabled,
                        showSnackBar = {
                            onEvent(
                                MainScreenEvent.ShowSnackBar(
                                    UiText.StringResource(
                                        R.string.please_turn_on_main_switch
                                    )
                                )
                            )
                        }
                    )
                    MainScreenCard(
                        text = stringResource(R.string.grayscale),
                        status = if (isGrayscaleEnabled) stringResource(id = R.string.enabled) else stringResource(
                            id = R.string.disabled
                        ),
                        iconId = R.drawable.ic_outline_color_lens_24,
                        onClick = { navController.navigate(Routes.SCREEN_GRAY_SCALE) },
                        mainSwitchEnabled = state.isMainSwitchEnabled,
                        showSnackBar = {
                            onEvent(
                                MainScreenEvent.ShowSnackBar(
                                    UiText.StringResource(
                                        R.string.please_turn_on_main_switch
                                    )
                                )
                            )
                        }
                    )
                    MainScreenCard(
                        text = stringResource(R.string.infinite_scrolling),
                        status = if (isInfiniteScrollBlockerEnabled) stringResource(id = R.string.enabled) else stringResource(
                            id = R.string.disabled
                        ),
                        iconId = R.drawable.ic_swipe_vertical_24,
                        onClick = { navController.navigate(Routes.INFINITE_SCROLLING) },
                        mainSwitchEnabled = state.isMainSwitchEnabled,
                        showSnackBar = {
                            onEvent(
                                MainScreenEvent.ShowSnackBar(
                                    UiText.StringResource(
                                        R.string.please_turn_on_main_switch
                                    )
                                )
                            )
                        }
                    )
                    MainScreenCard(
                        text = stringResource(R.string.delayed_launch),
                        status = if (isDelayedLaunchEnabled) stringResource(id = R.string.enabled) else stringResource(
                            id = R.string.disabled
                        ),
                        iconId = R.drawable.sharp_timelapse_24,
                        onClick = { navController.navigate(Routes.DELAYED_LAUNCH_SETTINGS) },
                        mainSwitchEnabled = state.isMainSwitchEnabled,
                        showSnackBar = {
                            onEvent(
                                MainScreenEvent.ShowSnackBar(
                                    UiText.StringResource(
                                        R.string.please_turn_on_main_switch
                                    )
                                )
                            )
                        }
                    )
                    /*MainScreenCard(
                        text = stringResource(R.string.launcher),
                        status = if (isLauncherEnabled) stringResource(id = R.string.enabled) else stringResource(
                            id = R.string.disabled
                        ),
                        iconId = null,
                        onClick = { navController.navigate(Routes.LAUNCHER_SETTINGS) },
                        mainSwitchEnabled = state.isMainSwitchEnabled,
                        showSnackBar = {
                            onEvent(
                                MainScreenEvent.ShowSnackBar(
                                    UiText.StringResource(
                                        R.string.please_turn_on_main_switch
                                    )
                                )
                            )
                        })*/
                }
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                /*Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MainScreenCard(
                        text = stringResource(R.string.launcher),
                        status = if (isLauncherEnabled) stringResource(id = R.string.enabled) else stringResource(
                            id = R.string.disabled
                        ),
                        iconId = null,
                        onClick = { navController.navigate(Routes.LAUNCHER_SETTINGS) })
                }*/
            }
        }
    }

    if (state.isUiModeDialogVisible) {
        UiModeDialog(onEvent, state)
    }
    if (state.isThemeColorsDialogVisible) {
        ThemeColorsDialog(onEvent = onEvent, state = state)
    }
    if (state.isCombineDbDialogVisible) {
        Dialog(
            onDismissRequest = {}
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(28.dp),
                tonalElevation = AlertDialogDefaults.TonalElevation,
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (state.isCombiningDb) {
                        Text(stringResource(R.string.please_wait))
                        LinearProgressIndicator()
                    } else {
                        Text(
                            stringResource(R.string.db_combine_dialog_title),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            stringResource(R.string.db_combine_dialog_text),
                            style = MaterialTheme.typography.bodySmall)
                        Row(modifier = Modifier) {
                            TextButton(onClick = {
                                onEvent(MainScreenEvent.OnCombineDbAgreeClick)
                            }) {
                                Text(stringResource(R.string.start_migrating))
                            }
                            TextButton(onClick = {
                                onEvent(MainScreenEvent.OnExitApp)
                            }) {
                                Text(stringResource(R.string.close_app))
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun TestCrashButton() {
    Button(onClick = { throw RuntimeException("Test crash") }) {
        Text("Simulate Crash")
    }
}