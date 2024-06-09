package ly.com.tahaben.core_ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.LocalSpacing
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.*
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.window.PopupProperties

@Composable
fun SearchTextField(
    text: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
    hint: String = stringResource(id = R.string.search),
    shouldShowHint: Boolean = false,
    onFocusChanged: (FocusState) -> Unit
) {
    val spacing = LocalSpacing.current
    val focusRequester = remember { FocusRequester() }
    Box(

    ) {
        BasicTextField(
            modifier = modifier
                .clip(RoundedCornerShape(5.dp))
                .padding(2.dp)
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
                .padding(spacing.spaceMedium)
                .padding(end = spacing.spaceMedium)
                .onFocusChanged { onFocusChanged(it) }
                .focusRequester(focusRequester),
            value = text,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch()
                    defaultKeyboardAction(ImeAction.Search)
                }
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
            ),
            textStyle = MaterialTheme.typography.headlineSmall.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface)
        )
        if (shouldShowHint) {
            Text(
                text = hint,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Light,
                color = Color.LightGray,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = spacing.spaceMedium)
            )
        }
        IconButton(
            onClick = onSearch,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(id = R.string.search)
            )
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownTextField(
    modifier: Modifier = Modifier,menuModifier: Modifier = Modifier,readOnly: Boolean = false, menuExpanded: Boolean, onExpandedChanged: (Boolean) -> Unit,
    text: String, onTextChange: (String) -> Unit, menuContent: @Composable ColumnScope.() -> Unit
) {
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = menuExpanded,
        onExpandedChange = onExpandedChanged
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            readOnly = readOnly,
            value = text,
            onValueChange = onTextChange,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpanded) })
        DropdownMenu(
            modifier = menuModifier.exposedDropdownSize(),
            expanded = menuExpanded,
            onDismissRequest = { onExpandedChanged(false) },
            properties = PopupProperties(focusable = false),
            content = menuContent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownTextField(
    modifier: Modifier = Modifier, menuExpanded: Boolean, onExpandedChanged: (Boolean) -> Unit,
    text: TextFieldValue, onTextChange: (TextFieldValue) -> Unit, menuContent: @Composable ColumnScope.() -> Unit
) {
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = menuExpanded,
        onExpandedChange = onExpandedChanged
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            value = text,
            onValueChange = onTextChange,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpanded) })
        DropdownMenu(
            modifier = Modifier.exposedDropdownSize(),
            expanded = menuExpanded,
            onDismissRequest = { onExpandedChanged(false) },
            properties = PopupProperties(focusable = false),
            content = menuContent
        )
    }
}

fun Modifier.moveDownOnTab(focusManager: FocusManager): Modifier = composed {
    this.onPreviewKeyEvent {
        if (it.key == Key.Tab && it.type == KeyEventType.KeyDown) {
            focusManager.moveFocus(FocusDirection.Down)
            true
        } else {
            false
        }
    }
}