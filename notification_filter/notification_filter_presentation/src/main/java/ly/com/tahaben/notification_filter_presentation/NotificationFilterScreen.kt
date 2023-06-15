package ly.com.tahaben.notification_filter_presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.*
import ly.com.tahaben.core_ui.components.HowDialog
import ly.com.tahaben.core_ui.components.PermissionNotGrantedContent
import ly.com.tahaben.notification_filter_domain.model.NotificationItem
import ly.com.tahaben.notification_filter_presentation.components.NotificationListItem
import ly.com.tahaben.showcase_layout_compose.model.Arrow
import ly.com.tahaben.showcase_layout_compose.model.Gravity
import ly.com.tahaben.showcase_layout_compose.model.ShowcaseMsg
import ly.com.tahaben.showcase_layout_compose.ui.ShowcaseLayout
import timber.log.Timber

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotificationFilterScreen(
    viewModel: NotificationFilterViewModel = hiltViewModel(),
    navigateToNotificationSettings: () -> Unit,
    onNavigateUp: () -> Unit
) {
    val spacing = LocalSpacing.current
    val state = viewModel.state
    val openDialog = remember { mutableStateOf(false) }

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
        initKey = 0,
        greeting = ShowcaseMsg(
            stringResource(R.string.notification_filter_showcase_greeting),
            msgBackground = Color.White,
            roundedCorner = 15.dp
        ),
        onFinish = {
            viewModel.setShowcased()
            isShowcasing = false
        }
    ) {
        Column {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.notifications))
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
                },
                actions = {
                    if (state.isPermissionGranted) {
                        IconButton(onClick = navigateToNotificationSettings) {
                            Showcase(
                                k = 1,
                                message = ShowcaseMsg(
                                    text = stringResource(R.string.notification_settings_shocase_tip),
                                    textStyle = TextStyle(Color.Black),
                                    gravity = Gravity.Auto,
                                    arrow = Arrow(curved = true),
                                    msgBackground = Color.White,
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.spaceMedium),
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
                                    style = MaterialTheme.typography.body2,
                                    color = Black,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = spacing.spaceSmall)
                        ) {
                            items(
                                state.filteredNotifications,
                                NotificationItem::id
                            ) { notificationItem ->
                                val dismissState = rememberDismissState(
                                    initialValue = DismissValue.Default,
                                    confirmStateChange = {
                                        if (it == DismissValue.DismissedToStart) {
                                            viewModel.onEvent(
                                                NotificationFilterEvent.OnDismissNotification(
                                                    notificationItem
                                                )
                                            )
                                        }
                                        true
                                    }
                                )
                                SwipeToDismiss(
                                    state = dismissState,
                                    directions = setOf(
                                        DismissDirection.EndToStart
                                    ),
                                    background = {
                                        val color by animateColorAsState(
                                            when (dismissState.targetValue) {
                                                DismissValue.Default -> White
                                                else -> Color.Red
                                            }
                                        )
                                        val alignment = Alignment.CenterEnd
                                        val icon = Icons.Default.Delete

                                        val scale by animateFloatAsState(
                                            targetValue =
                                            if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
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
                                                modifier = Modifier.scale(scale)
                                            )
                                        }
                                    },
                                    dismissContent = {
                                        Card(
                                            elevation = animateDpAsState(
                                                targetValue =
                                                if (dismissState.dismissDirection != null) 4.dp else 0.dp
                                            ).value,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(alignment = Alignment.CenterVertically)
                                        ) {
                                            NotificationListItem(
                                                notification = notificationItem,
                                                onClick = {
                                                    Timber.d("notification click composable")
                                                    viewModel.onEvent(
                                                        NotificationFilterEvent.OnOpenNotification(
                                                            notificationItem = notificationItem
                                                        )
                                                    )
                                                }
                                            )
                                        }
                                    }

                                )

                            }
                        }
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 0.dp, vertical = spacing.spaceSmall),
                            onClick = { viewModel.onEvent(NotificationFilterEvent.OnDeleteAllNotifications) }) {
                            Text(
                                text = stringResource(id = R.string.clear_all),
                                style = MaterialTheme.typography.button,
                                textAlign = TextAlign.Center
                            )
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