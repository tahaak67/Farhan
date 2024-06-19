package ly.com.tahaben.core_ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 12,Jun,2024
 */

@Composable
fun RadioRow(modifier: Modifier = Modifier, string: String, selected: Boolean, verticalAlignment: Alignment.Vertical = Alignment.Top, horizontalArrangement: Arrangement.Horizontal = Arrangement.Start, onClick: () -> Unit) {
    Row(
        modifier = modifier.selectable(
            selected = selected,
            role = Role.RadioButton,
            onClick = onClick
        ),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        Text(text = string)
        RadioButton(selected = selected, onClick = null)
    }
}

@Composable
fun CheckboxRow(modifier: Modifier = Modifier, string: String, selected: Boolean, verticalAlignment: Alignment.Vertical = Alignment.Top, horizontalArrangement: Arrangement.Horizontal = Arrangement.Start, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = modifier.toggleable(
            value = selected,
            role = Role.Checkbox,
            onValueChange = { onCheckedChange(it) }
        ),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        Text(text = string)
        Checkbox(checked = selected, onCheckedChange = null)
    }
}

@Composable
fun SwitchRow(modifier: Modifier = Modifier, string: String, selected: Boolean, verticalAlignment: Alignment.Vertical = Alignment.Top, horizontalArrangement: Arrangement.Horizontal = Arrangement.Start, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = modifier.toggleable(
            value = selected,
            role = Role.Switch,
            onValueChange = { onCheckedChange(it) }
        ),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        Text(modifier = Modifier.weight(1f),text = string)
        Switch(checked = selected, onCheckedChange = null)
    }
}

@Composable
fun SwitchRow(modifier: Modifier = Modifier,title: String, description: String, selected: Boolean, verticalAlignment: Alignment.Vertical = Alignment.Top, horizontalArrangement: Arrangement.Horizontal = Arrangement.Start, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = modifier.toggleable(
            value = selected,
            role = Role.Switch,
            onValueChange = { onCheckedChange(it) }
        ),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        Column(Modifier.weight(1f)) {
        Text(text = title, fontWeight = FontWeight.Bold)
        Text(text = description)
        }
        Switch(checked = selected, onCheckedChange = null)
    }
}
