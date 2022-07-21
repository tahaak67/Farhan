package ly.com.tahaben.core_ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.Black
import ly.com.tahaben.core_ui.LocalSpacing

@Composable
fun PermissionNotGrantedContent(
    modifier: Modifier,
    message: String,
    subMessage: String,
    onGrantClick: () -> Unit,
    onHowClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    Column(
        modifier = modifier
            .padding(horizontal = spacing.spaceMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.h2,
            color = Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(spacing.spaceMedium))
        Text(
            text = subMessage,
            style = MaterialTheme.typography.h3,
            color = Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(spacing.spaceMedium))
        Button(onClick = onGrantClick) {
            Text(
                text = stringResource(R.string.grant_access),
                style = MaterialTheme.typography.button,
                color = Black
            )
        }
        Spacer(modifier = Modifier.height(spacing.spaceMedium))
        Text(
            modifier = Modifier.clickable(onClick = onHowClick),
            text = stringResource(R.string.how),
            style = MaterialTheme.typography.button,
            color = Black
        )
    }
}