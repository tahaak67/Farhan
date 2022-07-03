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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.Black
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.OnLifecycleEvent
import ly.com.tahaben.core_ui.White
import ly.com.tahaben.notification_filter_domain.model.NotificationItem
import ly.com.tahaben.notification_filter_presentation.components.NotificationListItem
import timber.log.Timber

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotificationFilterScreen(
    viewModel: NotificationFilterViewModel = hiltViewModel(),
    navigateToNotificationSettings: () -> Unit
) {
    val spacing = LocalSpacing.current
    val state = viewModel.state


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
                Text(text = stringResource(id = R.string.notifications))

            },
            backgroundColor = White,
            actions = {
                IconButton(onClick = navigateToNotificationSettings) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = stringResource(id = R.string.notifications_filter_settings)
                    )
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.spaceMedium),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            if (!state.isServiceEnabled) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Notification permission is not granted :(",
                        style = MaterialTheme.typography.h1,
                        color = Black
                    )
                    Spacer(modifier = Modifier.height(spacing.spaceMedium))
                    Text(
                        text = "Farhan needs notification access for Notification filter to work",
                        style = MaterialTheme.typography.h3,
                        color = Black
                    )
                    Spacer(modifier = Modifier.height(spacing.spaceMedium))
                    Button(onClick = { viewModel.startNotificationService() }) {
                        Text(
                            text = "Grant access",
                            style = MaterialTheme.typography.button,
                            color = Black
                        )
                    }
                    Spacer(modifier = Modifier.height(spacing.spaceMedium))
                }

            } else {
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
                                text = "No notifications",
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
                                            contentDescription = "Delete Icon",
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