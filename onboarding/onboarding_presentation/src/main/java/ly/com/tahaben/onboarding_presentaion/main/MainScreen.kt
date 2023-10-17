package ly.com.tahaben.onboarding_presentaion.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import ly.com.tahaben.core.R
import ly.com.tahaben.core.navigation.Routes
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.onboarding_presentaion.components.MainScreenCard
import ly.com.tahaben.onboarding_presentaion.components.UiModeDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    tip: String,
    isGrayscaleEnabled: Boolean,
    isInfiniteScrollBlockerEnabled: Boolean,
    isNotificationFilterEnabled: Boolean,
    isLauncherEnabled: Boolean,
    navController: NavHostController,
    onEvent: (MainScreenEvent) -> Unit,
    state: MainScreenState
) {
    val spacing = LocalSpacing.current
    var mDisplayMenu by remember { mutableStateOf(false) }
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
            //backgroundColor = MaterialTheme.colors.surface,
            actions = {

                // Creating Icon button for dropdown menu
                IconButton(onClick = { mDisplayMenu = !mDisplayMenu }) {
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
                    contentDescription = stringResource(R.string.tip_icon_description)
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
                MainScreenCard(
                    text = stringResource(R.string.usage),
                    status = "",
                    iconId = R.drawable.ic_usage,
                    onClick = { navController.navigate(Routes.USAGE) })
                MainScreenCard(
                    text = stringResource(R.string.notifications_filter),
                    status = if (isNotificationFilterEnabled) stringResource(id = R.string.enabled) else stringResource(
                        id = R.string.disabled
                    ),
                    iconId = R.drawable.ic_notification,
                    onClick = { navController.navigate(Routes.NOTIFICATION_FILTER) })
                MainScreenCard(
                    text = stringResource(R.string.grayscale),
                    status = if (isGrayscaleEnabled) stringResource(id = R.string.enabled) else stringResource(
                        id = R.string.disabled
                    ),
                    iconId = R.drawable.ic_outline_color_lens_24,
                    onClick = { navController.navigate(Routes.SCREEN_GRAY_SCALE) })
                MainScreenCard(
                    text = stringResource(R.string.infinite_scrolling),
                    status = if (isInfiniteScrollBlockerEnabled) stringResource(id = R.string.enabled) else stringResource(
                        id = R.string.disabled
                    ),
                    iconId = R.drawable.ic_swipe_vertical_24,
                    onClick = { navController.navigate(Routes.INFINITE_SCROLLING) })
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

    if (state.isUiModeDialogVisible) {
        UiModeDialog(onEvent, state)
    }
}
