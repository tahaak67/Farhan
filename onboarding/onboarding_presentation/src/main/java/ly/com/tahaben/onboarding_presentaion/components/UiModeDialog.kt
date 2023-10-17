package ly.com.tahaben.onboarding_presentaion.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.domain.model.UIModeAppearance
import ly.com.tahaben.onboarding_presentaion.main.MainScreenEvent
import ly.com.tahaben.onboarding_presentaion.main.MainScreenState


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun UiModeDialog(
    onEvent: (MainScreenEvent) -> Unit,
    state: MainScreenState
) {
    val spacing = LocalSpacing.current
    AlertDialog(
        onDismissRequest = {
            onEvent(MainScreenEvent.DismissUiAppearanceDialog)
        }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column(
                modifier = Modifier.padding(spacing.spaceLarge)
            ) {
                Text(text = stringResource(id = R.string.appearance))
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                RadioButtonItem(
                    text = stringResource(R.string.dark),
                    isChecked = state.uiMode == UIModeAppearance.DARK_MODE
                ) {
                    onEvent(MainScreenEvent.SaveUiMode(UIModeAppearance.DARK_MODE))
                }
                Divider()
                RadioButtonItem(
                    text = stringResource(R.string.light),
                    isChecked = state.uiMode == UIModeAppearance.LIGHT_MODE
                ) {
                    onEvent(MainScreenEvent.SaveUiMode(UIModeAppearance.LIGHT_MODE))
                }
                Divider()
                RadioButtonItem(
                    text = stringResource(R.string.follow_system),
                    isChecked = state.uiMode == UIModeAppearance.FOLLOW_SYSTEM
                ) {
                    onEvent(MainScreenEvent.SaveUiMode(UIModeAppearance.FOLLOW_SYSTEM))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onEvent(MainScreenEvent.DismissUiAppearanceDialog) }) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RadioButtonItem(
    text: String,
    isChecked: Boolean,
    checked: () -> Unit
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier
            .padding(vertical = spacing.spaceSmall)
            .fillMaxWidth()
            .clickable(onClick = checked),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall
        )
        RadioButton(selected = isChecked, onClick = checked)
    }
}