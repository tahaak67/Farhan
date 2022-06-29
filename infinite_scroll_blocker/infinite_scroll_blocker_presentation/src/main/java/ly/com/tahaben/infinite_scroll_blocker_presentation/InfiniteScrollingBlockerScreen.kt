package ly.com.tahaben.infinite_scroll_blocker_presentation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun InfiniteScrollingBlockerScreen(
    onNavigateUp: () -> Unit,
    onNavigateToExceptions: () -> Unit,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {


}

