package ly.com.tahaben.screen_grayscale_presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.*

@Composable
fun GrayscaleScreen(
    onNavigateUp: () -> Unit,
    onNavigateToExceptions: () -> Unit,
    viewModel: GrayscaleViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current
    val state = viewModel.state
    val isServiceChecked = remember { mutableStateOf(state.isServiceEnabled) }
    isServiceChecked.value = state.isServiceEnabled

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.checkServiceStats()
                //viewModel.checkIfAppearOnTopPermissionGranted()
            }
            else -> Unit
        }
    }

    Column {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.grayscale))

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
            }
        )
        if (!state.isSecureSettingsPermissionGranted) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.spaceMedium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(
                        text = "Secure settings permission is not granted :(",
                        style = MaterialTheme.typography.h1,
                        color = Black
                    )
                    Spacer(modifier = Modifier.height(spacing.spaceMedium))
                    Text(
                        text = "Farhan needs Secure settings permission to grayscale the screen for you",
                        style = MaterialTheme.typography.h3,
                        color = Black
                    )
                    Spacer(modifier = Modifier.height(spacing.spaceMedium))
                    if (state.isDeviceRooted) {
                        Text(
                            text = "Looks like your device is rooted!, use the button below if you want to grant the permission with root",
                            style = MaterialTheme.typography.h3,
                            color = Black
                        )
                        Spacer(modifier = Modifier.height(spacing.spaceMedium))
                        Button(onClick = { viewModel.askForSecureSettingsPermissionWithRoot() }) {
                            Text(
                                text = "Grant access",
                                style = MaterialTheme.typography.button,
                                color = Black
                            )
                        }
                        Spacer(modifier = Modifier.height(spacing.spaceMedium))
                    }
                }
            }
        } else {
            Column(
                Modifier.fillMaxSize()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spacing.spaceExtraLarge)
                        .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceSmall),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,

                    ) {
                    Text(text = stringResource(R.string.enable_grayscale_for_white_listed_apps))
                    Switch(
                        checked = isServiceChecked.value,
                        onCheckedChange = { checked ->
                            viewModel.setServiceStats(checked)
                            isServiceChecked.value = checked
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spacing.spaceExtraLarge)
                        .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceSmall)
                        .clickable {
                            onNavigateToExceptions()
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.white_lited_apps),
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }
    }
}


