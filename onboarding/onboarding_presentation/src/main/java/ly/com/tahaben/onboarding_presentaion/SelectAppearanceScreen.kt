package ly.com.tahaben.onboarding_presentaion

import android.os.Build
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ly.com.tahaben.core.R
import ly.com.tahaben.core.util.ThemeColors
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.theme.FarhanTheme
import ly.com.tahaben.domain.model.UIModeAppearance
import ly.com.tahaben.onboarding_presentaion.components.isCurrentlyDark
import ly.com.tahaben.onboarding_presentaion.main.MainScreenEvent
import ly.com.tahaben.onboarding_presentaion.main.MainScreenState


@Composable
fun SelectAppearanceScreen(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit,
    onOkClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = spacing.spaceMedium)
            .selectableGroup(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(R.string.select_theme_colors),
            style = MaterialTheme.typography.displayMedium
        )
        Spacer(modifier = Modifier.height(spacing.spaceSmall))
        Text(
            text = stringResource(R.string.you_can_change_this_later_from_the_main_screen_menu),
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.height(spacing.spaceMedium))
        var lightTopLeft by remember {
            mutableStateOf(Offset(0f, 0f))
        }
        var darkTopLeft by remember {
            mutableStateOf(Offset(0f, 0f))
        }
        var followTopLeft by remember {
            mutableStateOf(Offset(0f, 0f))
        }
        val animatedOptionTopLeft by
        animateOffsetAsState(
            targetValue = when (state.uiMode) {
                UIModeAppearance.DARK_MODE -> darkTopLeft.plus(Offset(65f, 65f))
                UIModeAppearance.LIGHT_MODE -> lightTopLeft.plus(Offset(65f, 65f))
                UIModeAppearance.FOLLOW_SYSTEM -> followTopLeft.plus(Offset(65f, 65f))
            }, label = "selected option"
        )
        val rowBackgroundColor by animateColorAsState(
            targetValue = MaterialTheme.colorScheme.primary,
            label = "ui mode row background"
        )
        Row(
            modifier = Modifier
                .drawBehind {
                    drawRoundRect(
                        color = rowBackgroundColor,
                        size = size,
                        cornerRadius = CornerRadius(65f)
                    )
                    drawCircle(color = Color.White, radius = 50f, center = animatedOptionTopLeft)
                }
        ) {

            IconButton(
                modifier = Modifier.onGloballyPositioned {
                    darkTopLeft = it.positionInParent()
                },
                onClick = {
                    onEvent(MainScreenEvent.SaveUiMode(UIModeAppearance.DARK_MODE))
                }) {
                Icon(imageVector = Icons.Filled.DarkMode, contentDescription = "")
            }

            IconButton(
                modifier = Modifier.onGloballyPositioned {
                    followTopLeft = it.positionInParent()
                },
                onClick = {
                    onEvent(MainScreenEvent.SaveUiMode(UIModeAppearance.FOLLOW_SYSTEM))
                }) {
                Icon(imageVector = Icons.Filled.BrightnessAuto, contentDescription = "")
            }
            IconButton(
                modifier = Modifier.onGloballyPositioned {
                    lightTopLeft = it.positionInParent()
                },
                onClick = {
                    onEvent(MainScreenEvent.SaveUiMode(UIModeAppearance.LIGHT_MODE))
                }) {
                Icon(imageVector = Icons.Filled.LightMode, contentDescription = "")
            }
        }
        val activeModeName = when (state.uiMode) {
            UIModeAppearance.DARK_MODE -> stringResource(id = R.string.dark)
            UIModeAppearance.LIGHT_MODE -> stringResource(id = R.string.light)
            UIModeAppearance.FOLLOW_SYSTEM -> stringResource(id = R.string.follow_system)
        }

        Crossfade(targetState = activeModeName, label = "ui mode name") { text ->
            Text(modifier = Modifier.fillMaxWidth(), text = text, textAlign = TextAlign.Center)
        }
        Spacer(modifier = Modifier.height(spacing.spaceMedium))
        FarhanTheme(darkMode = state.uiMode.isCurrentlyDark(), colorStyle = ThemeColors.Classic) {
            ThemeColorsItem(
                state = state,
                onEvent = onEvent,
                themeColorsOption = ThemeColors.Classic,
                primary = MaterialTheme.colorScheme.primary,
                secondary = MaterialTheme.colorScheme.primaryContainer,
                surface = MaterialTheme.colorScheme.surface,
                tertiary = MaterialTheme.colorScheme.tertiary,
                name = stringResource(R.string.classic),
                description = stringResource(R.string.classic_colors_description)
            )
        }
        Spacer(modifier = Modifier.height(spacing.spaceSmall))
        FarhanTheme(darkMode = state.uiMode.isCurrentlyDark(), colorStyle = ThemeColors.Vibrant) {
            ThemeColorsItem(
                state = state,
                onEvent = onEvent,
                themeColorsOption = ThemeColors.Vibrant,
                primary = MaterialTheme.colorScheme.primary,
                secondary = MaterialTheme.colorScheme.primaryContainer,
                surface = MaterialTheme.colorScheme.surface,
                tertiary = MaterialTheme.colorScheme.tertiary,
                name = stringResource(R.string.vibrant),
                description = stringResource(R.string.vibrant_colors_description)
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Spacer(modifier = Modifier.height(spacing.spaceSmall))
            FarhanTheme(
                darkMode = state.uiMode.isCurrentlyDark(),
                colorStyle = ThemeColors.Dynamic
            ) {
                ThemeColorsItem(
                    state = state,
                    onEvent = onEvent,
                    themeColorsOption = ThemeColors.Dynamic,
                    primary = MaterialTheme.colorScheme.primary,
                    secondary = MaterialTheme.colorScheme.primaryContainer,
                    surface = MaterialTheme.colorScheme.surface,
                    tertiary = MaterialTheme.colorScheme.tertiary,
                    name = stringResource(R.string.dynamic),
                    description = stringResource(R.string.dynamic_colors_description)
                )
            }
        }
        Spacer(modifier = Modifier.height(spacing.spaceSmall))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onOkClick
        ) {
            Text(text = stringResource(id = R.string.ok))
        }
    }
}

@Composable
private fun ThemeColorsItem(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit,
    themeColorsOption: ThemeColors,
    primary: Color,
    secondary: Color,
    surface: Color,
    tertiary: Color,
    name: String,
    description: String
) {
    val itemContentDescription = stringResource(R.string.theme_colors_for, name)
    val spacing = LocalSpacing.current
    val isSelectedMode = state.themeColors == themeColorsOption
    Card(
        modifier = Modifier
            .animateContentSize()
            .height(if (isSelectedMode) 100.dp else 75.dp)
            .selectable(
                selected = isSelectedMode,
                onClick = { onEvent(MainScreenEvent.SaveThemeColorsMode(themeColorsOption)) },
                role = Role.RadioButton
            )
            .semantics { contentDescription = itemContentDescription },
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .height(if (isSelectedMode) 65.dp else 75.dp)
                .fillMaxWidth()
                .padding(end = spacing.spaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelectedMode,
                    onClick = { onEvent(MainScreenEvent.SaveThemeColorsMode(themeColorsOption)) }
                )
                Text(text = name)
            }
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(spacing.spaceSmall)
            ) {
                Card(
                    modifier = Modifier
                        .size(50.dp), colors = CardDefaults.cardColors(containerColor = primary)
                ) {}
                Card(
                    modifier = Modifier
                        .size(50.dp), colors = CardDefaults.cardColors(containerColor = secondary)
                ) {}
                Card(
                    modifier = Modifier
                        .size(50.dp), colors = CardDefaults.cardColors(containerColor = surface)
                ) {}
                Card(
                    modifier = Modifier
                        .size(50.dp), colors = CardDefaults.cardColors(containerColor = tertiary)
                ) {}
            }

        }
        Text(
            modifier = Modifier.padding(horizontal = spacing.spaceSmall),
            text = description
        )
    }
}

@Preview
@Composable
private fun SelectAppearanceScreenPreview() {
    SelectAppearanceScreen(state = MainScreenState(), {}, {})
}