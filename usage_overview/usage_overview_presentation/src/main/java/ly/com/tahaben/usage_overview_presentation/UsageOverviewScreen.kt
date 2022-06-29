package ly.com.tahaben.usage_overview_presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.White
import ly.com.tahaben.usage_overview_presentation.components.DaySelector
import ly.com.tahaben.usage_overview_presentation.components.TrackedAppItem
import ly.com.tahaben.usage_overview_presentation.components.UsageOverviewHeader


@Composable
fun UsageOverviewScreen(
    onNavigateUp: () -> Unit,
    viewModel: UsageOverviewViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current
    val state = viewModel.state
    val context = LocalContext.current

    Column {
        TopAppBar(
            title = { Text(text = stringResource(id = R.string.usage)) },
            backgroundColor = White,
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
            }
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = spacing.spaceMedium)
        ) {
            item {
                UsageOverviewHeader(state = state)
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                DaySelector(
                    date = state.date,
                    isToday = state.isDateToday,
                    isLoading = state.isLoading,
                    onPreviousDayClick = {
                        viewModel.onEvent(UsageOverviewEvent.OnPreviousDayClick)
                    },
                    onNextDayClick = {
                        viewModel.onEvent(UsageOverviewEvent.OnNextDayClick)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.spaceMedium)
                )
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
            }

            items(state.trackedApps) { app ->
                TrackedAppItem(
                    trackedApp = app,
                )
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> {
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                CircularProgressIndicator()
            }
            state.trackedApps.isEmpty() -> {
                Text(
                    text = stringResource(id = R.string.no_results),
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }


}