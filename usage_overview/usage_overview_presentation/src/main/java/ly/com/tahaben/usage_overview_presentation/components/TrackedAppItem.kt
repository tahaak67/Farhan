package ly.com.tahaben.usage_overview_presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ly.com.tahaben.core_ui.LocalSpacing
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
            .padding(spacing.spaceExtraSmall)
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(5.dp)
            )
            .background(MaterialTheme.colors.surface)
            .padding(end = spacing.spaceMedium)
            .height(100.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Spacer(modifier = Modifier.width(spacing.spaceMedium))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = trackedApp.appName,
                style = MaterialTheme.typography.body1,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(spacing.spaceExtraSmall))
            Text(
                text = trackedApp.appCategoryName.asString(context),
                style = MaterialTheme.typography.body2
            )
            Spacer(modifier = Modifier.height(spacing.spaceExtraSmall))
            Text(
                text = trackedApp.usageDuration.asString(context),
                style = MaterialTheme.typography.body2
            )
        }
        Spacer(modifier = Modifier.width(spacing.spaceMedium))
    }
}