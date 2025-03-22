package ly.com.tahaben.launcher_presentation.settings

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.OnLifecycleEvent
import ly.com.tahaben.core_ui.mirror

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LauncherSettingsScreen(
    onNavigateUp: () -> Unit,
    onNavigateToTimeLimiter: () -> Unit,
    viewModel: LauncherSettingsViewModel = hiltViewModel(),
    onNavigateToDelayedLaunch: () -> Unit
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val state = viewModel.state
    val launcherEnabled = remember { mutableStateOf(state.isLauncherEnabled) }
    var screenLockDetectorEnabled by remember { mutableStateOf(false) }


    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.checkLauncherStats()
            }
            else -> Unit
        }
    }
    Column(
        Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.launcher_settings))
            },
//            backgroundColor = White,
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        modifier = Modifier.mirror(),
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
            },
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,

            ) {
            Text(text = stringResource(R.string.launcher))
            Switch(
                checked = state.isLauncherEnabled,
                onCheckedChange = { checked ->
                    viewModel.openLauncherSettings()
                    if (checked) {
                        Toast.makeText(
                            context,
                            R.string.set_default_launcher_text,
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            R.string.set_other_default_launcher_text,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(spacing.spaceMedium))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceMedium)
                .clickable {
                    onNavigateToTimeLimiter()
                },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(R.string.app_time_limiter))
        }
        Spacer(modifier = Modifier.height(spacing.spaceMedium))
    }
}
