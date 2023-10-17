package ly.com.tahaben.usage_overview_presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ly.com.tahaben.core.util.UiText
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.Page
import ly.com.tahaben.usage_overview_domain.model.UsageDataItem
import ly.com.tahaben.usage_overview_domain.model.UsageDurationDataItem


@Composable
fun TrackedAppItem(
    trackedApp: UsageDurationDataItem,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .padding(horizontal = spacing.spaceMedium)
            .border(width = 0.5.dp, color = Page, shape = RoundedCornerShape(5.dp))
            .fillMaxWidth(),
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 70.dp, max = 100.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(spacing.spaceMedium))
            Text(
                modifier = Modifier.weight(1f),
                text = trackedApp.appName,
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                textAlign = TextAlign.Start
            )
            Text(
                modifier = Modifier.weight(1f),
                text = trackedApp.appCategoryName.asString(context),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.weight(1f),
                text = trackedApp.usageDuration.asString(context),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.End
            )
            Spacer(modifier = Modifier.width(spacing.spaceMedium))
        }
    }
}

@Preview
@Composable
private fun TrackedAppItemPreview() {
    val trackedApp = UsageDurationDataItem(
        appName = "Farhan",
        packageName = "ly.farhan",
        usageDuration = UiText.DynamicString("23m 1h"),
        appCategory = UsageDataItem.Category.PRODUCTIVITY,
        appCategoryName = UiText.DynamicString("Productivity"),
        usageDurationInMilliseconds = 10000L
    )
    TrackedAppItem(trackedApp = trackedApp)
}