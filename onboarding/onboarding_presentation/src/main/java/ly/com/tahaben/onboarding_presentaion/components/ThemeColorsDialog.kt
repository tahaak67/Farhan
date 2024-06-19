package ly.com.tahaben.onboarding_presentaion.components

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import ly.com.tahaben.core.R
import ly.com.tahaben.core.model.ThemeColors
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.onboarding_presentaion.main.MainScreenEvent
import ly.com.tahaben.onboarding_presentaion.main.MainScreenState


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ThemeColorsDialog(
    onEvent: (MainScreenEvent) -> Unit,
    state: MainScreenState
) {
    val spacing = LocalSpacing.current
    AlertDialog(
        onDismissRequest = {
            onEvent(MainScreenEvent.DismissThemeColorsDialog)
        }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column(
                modifier = Modifier
                    .padding(spacing.spaceLarge)
                    .selectableGroup()
            ) {
                Text(text = stringResource(id = R.string.theme_colors))
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                RadioButtonItem(
                    modifier = Modifier
                        .selectable(
                            selected = state.themeColors == ThemeColors.Classic,
                            onClick = { onEvent(MainScreenEvent.SaveThemeColorsMode(ThemeColors.Classic)) },
                            role = Role.RadioButton
                        ),
                    text = stringResource(R.string.classic),
                    isChecked = state.themeColors == ThemeColors.Classic
                ) {
                    onEvent(MainScreenEvent.SaveThemeColorsMode(ThemeColors.Classic))
                }
                Divider()
                RadioButtonItem(
                    modifier = Modifier
                        .selectable(
                            selected = state.themeColors == ThemeColors.Vibrant,
                            onClick = { onEvent(MainScreenEvent.SaveThemeColorsMode(ThemeColors.Vibrant)) },
                            role = Role.RadioButton
                        ),
                    text = stringResource(R.string.vibrant),
                    isChecked = state.themeColors == ThemeColors.Vibrant
                ) {
                    onEvent(MainScreenEvent.SaveThemeColorsMode(ThemeColors.Vibrant))
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Divider()
                    RadioButtonItem(
                        modifier = Modifier
                            .selectable(
                                selected = state.themeColors == ThemeColors.Dynamic,
                                onClick = { onEvent(MainScreenEvent.SaveThemeColorsMode(ThemeColors.Dynamic)) },
                                role = Role.RadioButton
                            ),
                        text = stringResource(R.string.dynamic),
                        isChecked = state.themeColors == ThemeColors.Dynamic
                    ) {
                        onEvent(MainScreenEvent.SaveThemeColorsMode(ThemeColors.Dynamic))
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onEvent(MainScreenEvent.DismissThemeColorsDialog) }) {
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
    modifier: Modifier,
    text: String,
    isChecked: Boolean,
    checked: () -> Unit
) {
    val spacing = LocalSpacing.current
    val itemDescription = stringResource(id = R.string.theme_colors_for, text)
    Row(
        modifier = modifier
            .padding(vertical = spacing.spaceSmall)
            .fillMaxWidth(),
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