package ly.com.tahaben.core_ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.LocalSpacing

@Composable
fun PermissionNotGrantedContent(
    modifier: Modifier,
    message: String,
    subMessage: String,
    permissionReasons: AnnotatedString? = null,
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
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(spacing.spaceMedium))
        Text(
            text = subMessage,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(spacing.spaceMedium))
        if (permissionReasons != null) {
            Text(
                text = permissionReasons,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(spacing.spaceMedium))
        }
        Button(onClick = onGrantClick) {
            Text(
                text = stringResource(R.string.grant_access),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(spacing.spaceMedium))
        Text(
            modifier = Modifier.clickable(onClick = onHowClick),
            text = stringResource(R.string.how),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}