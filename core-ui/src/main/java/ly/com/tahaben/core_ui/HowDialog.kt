package ly.com.tahaben.core_ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ly.com.tahaben.core.R

@Composable
fun HowDialog(
    @DrawableRes gifId: Int,
    gifDescription: String,
    onDismiss: () -> Unit
) {
    val spacing = LocalSpacing.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(),
    ) {
        Column(
            modifier = Modifier
                .background(White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GifImage(
                modifier = Modifier
                    .height(550.dp)
                    .fillMaxWidth(),
                gifId = gifId,
                gifDescription = gifDescription
            )
            Spacer(modifier = Modifier.height(spacing.spaceMedium))
            Button(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.ok),
                    style = MaterialTheme.typography.button
                )
            }
        }
    }
}
