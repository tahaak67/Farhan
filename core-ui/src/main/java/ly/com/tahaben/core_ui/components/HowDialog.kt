package ly.com.tahaben.core_ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.White

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
                .background(White)
                .verticalScroll(rememberScrollState()),
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
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
