package ly.com.tahaben.screen_grayscale_presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.screen_grayscale_domain.model.GrayscaleAppState
import ly.com.tahaben.screen_grayscale_presentation.exceptions.GrayscaleApp


@Composable
fun GrayscaleAppListItem(
    grayscaleApp: GrayscaleApp,
    onStateChange: (GrayscaleAppState) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .padding(spacing.spaceExtraSmall)
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(5.dp)
            )
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceSmall)
            .fillMaxWidth()
    ) {
        Text(
            text = grayscaleApp.app.name ?: "",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )
        Spacer(modifier = Modifier.height(spacing.spaceExtraSmall))
        Text(
            text = grayscaleApp.app.category ?: "",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(spacing.spaceSmall))
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            GrayscaleAppState.entries.forEachIndexed { index, appState ->
                SegmentedButton(
                    selected = grayscaleApp.grayscaleState == appState,
                    onClick = { onStateChange(appState) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = GrayscaleAppState.entries.size
                    )
                ) {
                    Text(
                        text = stringResource(id = appState.label),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private val GrayscaleAppState.label: Int
    @StringRes get() = when (this) {
        GrayscaleAppState.COLOR -> R.string.grayscale_state_color
        GrayscaleAppState.LEAVE_AS_IS -> R.string.grayscale_state_leave_as_is
        GrayscaleAppState.GRAYSCALE -> R.string.grayscale
    }
