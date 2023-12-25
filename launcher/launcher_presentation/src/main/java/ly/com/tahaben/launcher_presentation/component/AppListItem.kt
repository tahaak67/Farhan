package ly.com.tahaben.launcher_presentation.component

//import ly.com.tahaben.core_ui.Black
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ly.com.tahaben.core.model.AppItem
import ly.com.tahaben.core_ui.LocalSpacing

//import ly.com.tahaben.core_ui.White


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppListItem(
    app: AppItem,
    onItemClick: () -> Unit,
    onItemLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Row(
        modifier = modifier
            .padding(spacing.spaceExtraSmall)
            .height(100.dp)
            .combinedClickable(
                onClick =
                onItemClick,
                onLongClick =
                onItemLongClick

            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Spacer(modifier = Modifier.width(spacing.spaceMedium))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = app.name ?: "",
                style = MaterialTheme.typography.body1.copy(
                    shadow = Shadow(
//                        color = Black,
                        offset = Offset(4f, 4f),
                        blurRadius = 8f
                    )
                ),
                overflow = TextOverflow.Ellipsis,
//                color = White
            )
            Spacer(modifier = Modifier.height(spacing.spaceExtraSmall))
        }
    }
}