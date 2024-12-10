package ly.com.tahaben.launcher_presentation.wait

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import ly.com.tahaben.core.R
import ly.com.tahaben.core.model.ThemeColors
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.components.SwitchRow
import ly.com.tahaben.core_ui.theme.FarhanTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MindfulLaunchScreen(
    onNavigateUp: () -> Unit,
    onNavigateToWhitelist: () -> Unit,
    onEvent: (MindfulLaunchEvent) -> Unit,
    state: MindfulLaunchState
) {
    val spacing = LocalSpacing.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Mindful launch")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            modifier = Modifier,
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            SwitchRow(
                Modifier
                    .fillMaxWidth()
                    .padding(spacing.spaceMedium),
                "Mindful launch",
                selected = state.isMindfulLaunchEnabled,
                verticalAlignment = Alignment.CenterVertically
            ) {
                onEvent(MindfulLaunchEvent.OnMindfulLaunchEnabled(it))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceMedium)
                    .clickable {
                        onNavigateToWhitelist()
                    },
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.white_lited_apps),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Preview()
@Preview("ar", locale = "ar")
@Composable
private fun MindfulLaunchScreenPreview() {

    FarhanTheme(false, ThemeColors.Classic) {
        MindfulLaunchScreen(
            onNavigateUp = {},
            onNavigateToWhitelist = {},
            onEvent = {},
            state = MindfulLaunchState()
        )
    }
}