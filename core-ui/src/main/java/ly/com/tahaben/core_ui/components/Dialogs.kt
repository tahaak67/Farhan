package ly.com.tahaben.core_ui.components

import android.os.Build
import android.widget.NumberPicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.LocalSpacing
import timber.log.Timber

/**
 * A Simple number picker dialog.
 * @param onValueChangedListener a listener that returns the old value and the new value */
@Composable
fun NumberPickerDialog(
    onDismissDialog: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    initialValue: Int,
    minValue: Int,
    maxValue: Int,
    unit: String,
    onValueChangedListener: (Int,Int) -> Unit,
    onConfirmValue: (Int) -> Unit
) {
    val spacing = LocalSpacing.current
    var currentValue by remember {
        mutableIntStateOf(initialValue)
    }
    Dialog(
        onDismissRequest = onDismissDialog,
        properties = DialogProperties(),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = androidx.compose.ui.Modifier.height(spacing.spaceMedium))
                Text(
                    text = stringResource(R.string.remind_me_to_stop_scrolling_after),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Spacer(modifier = androidx.compose.ui.Modifier.height(spacing.spaceMedium))
                Row {
                    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
                    AndroidView(
                        factory = { context ->
                            val np = NumberPicker(context)
                            np.maxValue = maxValue
                            np.minValue = minValue
                            np.value = initialValue
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                np.textColor = textColor
                            }
                            np.setOnValueChangedListener { _, i, i2 ->
                                Timber.d("np: oldv: $i newv: $i2")
                                onValueChangedListener(i,i2)
                                currentValue = i2
                            }
                            np
                        })
                    Text(
                        modifier = Modifier
                            .padding(horizontal = spacing.spaceExtraSmall)
                            .align(Alignment.CenterVertically),
                        text = unit,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }
                Spacer(modifier = androidx.compose.ui.Modifier.height(spacing.spaceMedium))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            onDismissDialog()
                        }
                    ) {
                        Text(
                            stringResource(R.string.dismiss),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    TextButton(
                        onClick = { onConfirmValue(currentValue) }
                    ) {
                        Text(
                            stringResource(id = R.string.confirm),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
                Spacer(modifier = androidx.compose.ui.Modifier.height(spacing.spaceMedium))
            }
        }
    }
}


@Composable
fun MyDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    content: @Composable() (ColumnScope.() -> Unit)
) {
    val spacing = LocalSpacing.current
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(modifier = modifier.padding(spacing.spaceMedium), verticalArrangement = Arrangement.spacedBy(spacing.spaceMedium)) {
                content()
            }
        }
    }
}