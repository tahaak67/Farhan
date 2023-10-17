package ly.com.tahaben.core_ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ly.com.tahaben.core.model.AppItem
import ly.com.tahaben.core_ui.LocalSpacing


@Composable
fun AppExceptionListItem(
    app: AppItem,
    onClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .padding(spacing.spaceExtraSmall)

            .border(
                width = 0.25.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f),
                shape = RoundedCornerShape(5.dp)
            )
            .background(MaterialTheme.colorScheme.surface)
            .padding(end = spacing.spaceMedium)
            .height(100.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Spacer(modifier = Modifier.width(spacing.spaceMedium))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = app.name ?: "",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(spacing.spaceExtraSmall))
            Text(
                text = app.category ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(spacing.spaceExtraSmall))

        }
        Spacer(modifier = Modifier.width(spacing.spaceMedium))
        val isChecked = remember { mutableStateOf(app.isException) }
        isChecked.value = app.isException
        Switch(
            checked = isChecked.value,
            onCheckedChange = { isCkd ->
                isChecked.value = isCkd
                app.isException = isCkd
                onClick(isCkd)
            },
            enabled = true
        )
    }
}