package ly.com.tahaben.notification_filter_presentation

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.OnLifecycleEvent
import ly.com.tahaben.core_ui.components.HowDialog
import ly.com.tahaben.core_ui.components.PermissionNotGrantedContent
import ly.com.tahaben.core_ui.mirror
import ly.com.tahaben.notification_filter_domain.model.NotificationItem
import ly.com.tahaben.notification_filter_presentation.components.MenuOption
import ly.com.tahaben.notification_filter_presentation.components.NotificationListItem
import ly.com.tahaben.showcase_layout_compose.model.Arrow
import ly.com.tahaben.showcase_layout_compose.model.Gravity
import ly.com.tahaben.showcase_layout_compose.model.ShowcaseMsg
import ly.com.tahaben.showcase_layout_compose.ui.ShowcaseLayout
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NotificationFilterScreen(
    viewModel: NotificationFilterViewModel = hiltViewModel(),
    navigateToNotificationSettings: () -> Unit,
    onNavigateUp: () -> Unit,
    isUiModeDark: Boolean
) {
    val spacing = LocalSpacing.current
    val state = viewModel.state
    val openDialog = remember { mutableStateOf(false) }
    val density = LocalDensity.current
    val context = LocalContext.current
    val localLayoutDirection = LocalLayoutDirection.current

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.checkServiceStats()
            }

            else -> Unit
        }
    }

    var isShowcasing by remember { mutableStateOf(false) }
    Timber.d("isShowcasing value = $isShowcasing")
    ShowcaseLayout(
        isShowcasing = isShowcasing,
        initIndex = 0,
        greeting = ShowcaseMsg(
            stringResource(R.string.notification_filter_showcase_greeting),
            msgBackground = MaterialTheme.colorScheme.surface,
            roundedCorner = 15.dp
        ),
        onFinish = {
            viewModel.setShowcased()
            isShowcasing = false
        },
        isDarkLayout = false
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.notifications))
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
                    },
                    actions = {
                        if (state.isPermissionGranted) {
                            IconButton(onClick = navigateToNotificationSettings) {
                                Showcase(
                                    index = 1,
                                    message = ShowcaseMsg(
                                        text = stringResource(R.string.notification_settings_shocase_tip),
                                        textStyle = TextStyle(MaterialTheme.colorScheme.onSurface),
                                        gravity = Gravity.Auto,
                                        arrow = Arrow(curved = true),
                                        msgBackground = MaterialTheme.colorScheme.surface,
                                        roundedCorner = 15.dp
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription = stringResource(id = R.string.notifications_filter_settings)
                                    )
                                }
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                if (!state.isPermissionGranted) {
                    PermissionNotGrantedContent(
                        modifier = Modifier.fillMaxSize(),
                        message = stringResource(R.string.notification_permission_not_granted),
                        subMessage = stringResource(R.string.notification_filter_needed_message),
                        onGrantClick = viewModel::startNotificationService,
                        onHowClick = { openDialog.value = true }
                    )
                } else {
                    if (state.isFirstTimeOpened) {
                        Timber.d("shouldshowcase = true")
                        isShowcasing = true
                    }
                    if (state.filteredNotifications.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.no_notifications),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                    } else {
                        Box(modifier = Modifier) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
                                contentPadding = PaddingValues(
                                    top = spacing.spaceSmall,
                                    bottom = spacing.spaceHuge + spacing.spaceSmall
                                )
                            ) {
                                items(
                                    state.filteredNotifications,
                                    NotificationItem::id
                                ) { notificationItem ->
                                    val dismissState = rememberSwipeToDismissBoxState(
                                        initialValue = SwipeToDismissBoxValue.Settled,
                                        // Require dragging ~half the row width before committing instead of
                                        // the default flat 56.dp, which made accidental dismisses very easy.
                                        positionalThreshold = { totalDistance -> totalDistance * 0.5f }
                                    )

                                    // Trigger the dismiss only once the box has actually SETTLED at the
                                    // EndToStart anchor — i.e. after the finger is lifted and the swipe-out
                                    // animation completes. We deliberately do NOT use confirmValueChange for
                                    // this: it's invoked mid-drag, and vetoing Settled there both blocks the
                                    // snap-back and makes the release fling fall back to a stale value, which
                                    // dismisses the row even after you drag it back to the start.
                                    LaunchedEffect(dismissState.settledValue) {
                                        if (dismissState.settledValue == SwipeToDismissBoxValue.EndToStart) {
                                            viewModel.onEvent(
                                                NotificationFilterEvent.OnDismissNotification(notificationItem)
                                            )
                                        }
                                    }

                                    // Using CompositionLocalProvider to change the layout direction because of bug in SwipeToDismissBox
                                    // in RTL layouts
                                    CompositionLocalProvider(value = LocalLayoutDirection provides LayoutDirection.Ltr) {
                                        SwipeToDismissBox(
                                            modifier = Modifier.animateItem(placementSpec = tween(200)),
                                            state = dismissState,
                                            enableDismissFromStartToEnd = false,
                                            backgroundContent = {
                                                val color by animateColorAsState(
                                                    when (dismissState.targetValue) {
                                                        SwipeToDismissBoxValue.Settled -> MaterialTheme.colorScheme.background
                                                        else -> MaterialTheme.colorScheme.error
                                                    }
                                                )
                                                val alignment = Alignment.CenterEnd
                                                val icon = Icons.Default.Delete

                                                val scale by animateFloatAsState(
                                                    targetValue =
                                                    if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 0.75f else 1f
                                                )
                                                Box(
                                                    modifier =
                                                    Modifier
                                                        .fillMaxSize()
                                                        .background(color)
                                                        .padding(horizontal = 20.dp),
                                                    contentAlignment = alignment
                                                ) {
                                                    Icon(
                                                        imageVector = icon,
                                                        contentDescription = stringResource(R.string.delete_icon),
                                                        modifier = Modifier.scale(scale),
                                                        tint = MaterialTheme.colorScheme.onError
                                                    )
                                                }
                                            },
                                            content = {
                                                // Wrapping content with the correct layout direction to avoid LTR layout force by the statement above
                                                CompositionLocalProvider(value = LocalLayoutDirection provides localLayoutDirection) {
                                                    Card(
                                                        elevation = CardDefaults.cardElevation(
                                                            defaultElevation = 0.dp,
                                                            draggedElevation = 4.dp
                                                        ),
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(horizontal = spacing.spaceMedium)
                                                            .fillMaxWidth()
                                                            .align(alignment = Alignment.CenterVertically)
                                                    ) {
                                                        NotificationListItem(
                                                            modifier = Modifier,
                                                            notification = notificationItem,
                                                            onClick = {
                                                                Timber.d("notification click composable")
                                                                viewModel.onEvent(
                                                                    NotificationFilterEvent.OnOpenNotification(
                                                                        notificationItem = notificationItem
                                                                    )
                                                                )
                                                            },
                                                            menuOptions = listOf(
                                                                MenuOption(
                                                                    text = stringResource(id = R.string.exclude_this_app),
                                                                    onClick = {
                                                                        viewModel.onEvent(
                                                                            NotificationFilterEvent.OnExcludeAppClick(
                                                                                notificationItem.packageName
                                                                            )
                                                                        )
                                                                        Toast.makeText(
                                                                            context,
                                                                            R.string.app_excluded,
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    }),
                                                                MenuOption(
                                                                    text = stringResource(id = R.string.app_info),
                                                                    onClick = {
                                                                        viewModel.onEvent(
                                                                            NotificationFilterEvent.OnLaunchAppInfoClick(
                                                                                notificationItem.packageName
                                                                            )
                                                                        )
                                                                    }),
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        )

                                    }

                                }
                            }
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(spacing.spaceHuge)
                                    .padding(
                                        vertical = spacing.spaceSmall,
                                        horizontal = spacing.spaceMedium
                                    )
                                    .align(Alignment.BottomCenter),
                                onClick = { viewModel.onEvent(NotificationFilterEvent.OnDeleteAllNotifications) }) {
                                Text(
                                    text = stringResource(id = R.string.clear_all),
                                    style = MaterialTheme.typography.labelLarge,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if (openDialog.value) {
        HowDialog(
            gifId = R.drawable.notification_permission_howto,
            gifDescription = stringResource(R.string.how_to_enable_permission),
            onDismiss = { openDialog.value = false }
        )
    }
}