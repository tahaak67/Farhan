package ly.com.tahaben.core_ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.Black
import ly.com.tahaben.core_ui.LocalSpacing

@Composable
fun AccessibilityNotRunningContent(
    modifier: Modifier,
    message: String,
    subMessage: String,
    permissionReasons: AnnotatedString? = null,
    onGrantClick: () -> Unit,
    onBack: () -> Unit,
    onHowClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    val accessibilityServiceNotRunningScrollState = rememberScrollState()
    Column(
        modifier = modifier
            .padding(horizontal = spacing.spaceMedium)
            .verticalScroll(accessibilityServiceNotRunningScrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.h3,
            color = Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(spacing.spaceMedium))
        Text(
            text = subMessage,
            style = MaterialTheme.typography.h4,
            color = Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(spacing.spaceMedium))
        if (permissionReasons != null) {
            Text(
                text = permissionReasons,
                style = MaterialTheme.typography.h4,
                color = Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(spacing.spaceMedium))
        }
        Text(
            text = stringResource(R.string.if_you_choose_agree_msg),
            style = MaterialTheme.typography.h4,
            color = Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(spacing.spaceMedium))
        Row {
            Button(onClick = onGrantClick) {
                Text(
                    text = stringResource(R.string.agree),
                    style = MaterialTheme.typography.button,
                    color = Black
                )
            }
            Spacer(modifier = Modifier.width(spacing.spaceSmall))
            Button(onClick = onBack) {
                Text(
                    text = stringResource(R.string.cancel),
                    style = MaterialTheme.typography.button,
                    color = Black
                )
            }
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