package ly.com.tahaben.launcher_presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.components.CommonAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LauncherSettingsScreen(
    onNavigateUp: () -> Unit,
    onNavigateToTimeLimiter: () -> Unit,
    viewModel: LauncherSettingsViewModel = hiltViewModel(),
    onNavigateToDelayedLaunch: () -> Unit
) {
    val spacing = LocalSpacing.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CommonAppBar(
                title = stringResource(id = R.string.launcher_settings),
                onNavigateUp = onNavigateUp
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.openLauncherSettings()
                    },
                headlineContent = {
                    Text(
                        text = stringResource(R.string.change_default_launcher),
                        textAlign = TextAlign.Start
                    )
                },
                supportingContent = {
                    Text(text = stringResource(R.string.change_default_launcher_sub))
                },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background)
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = spacing.spaceMedium))
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onNavigateToTimeLimiter()
                    },
                headlineContent = {
                    Text(
                        text = stringResource(R.string.app_time_limiter),
                        textAlign = TextAlign.Start
                    )
                },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    }
}
