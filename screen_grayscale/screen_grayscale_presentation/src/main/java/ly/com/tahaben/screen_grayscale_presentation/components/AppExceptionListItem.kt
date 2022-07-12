package ly.com.tahaben.screen_grayscale_presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(5.dp)
            )
            .background(MaterialTheme.colors.surface)
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
                style = MaterialTheme.typography.body1,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(spacing.spaceExtraSmall))
            Text(
                text = app.category ?: "",
                style = MaterialTheme.typography.body2
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