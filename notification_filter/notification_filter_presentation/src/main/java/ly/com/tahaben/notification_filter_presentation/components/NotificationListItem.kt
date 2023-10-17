package ly.com.tahaben.notification_filter_presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.notification_filter_domain.model.NotificationItem


@Composable
fun NotificationListItem(
    notification: NotificationItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(horizontal = spacing.spaceExtraSmall, vertical = spacing.spaceSmall)

            .clickable {
                onClick()
            },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {

        Spacer(modifier = Modifier.width(spacing.spaceMedium))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.alignByBaseline(),
                    text = notification.appName ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Normal,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                Text(
                    modifier = Modifier.alignByBaseline(),
                    text = notification.time,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = notification.title ?: "",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(spacing.spaceSmall))
            Text(
                text = notification.text ?: "",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
            )
//            Spacer(modifier = Modifier.height(spacing.spaceSmall))
        }

        Spacer(modifier = Modifier.width(spacing.spaceMedium))
    }
}