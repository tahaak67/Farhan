package ly.com.tahaben.usage_overview_presentation.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ly.com.tahaben.core.R

@Composable
fun ConfirmDeleteDialog(
    onDismiss: () -> Unit,
    date: String,
    onConfirm: () -> Unit
) {

    AlertDialog(onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(id = R.string.delete_cache_dialog_title, date))
        },
        text = {
            Text(text = stringResource(id = R.string.delete_cache_dialog_text, date))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel))
            }
        })
}