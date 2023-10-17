package ly.com.tahaben.usage_overview_presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.mirror
import java.time.LocalDate

@Composable
fun DaySelector(
    date: LocalDate,
    isToday: Boolean,
    isLoading: Boolean,
    onPreviousDayClick: () -> Unit,
    onNextDayClick: () -> Unit,
    onDayClick: () -> Unit,
    modifier: Modifier = Modifier,
    isRangeMode: Boolean,
    dateRangeStart: LocalDate?,
    dateRangeEnd: LocalDate?
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = modifier,
        horizontalArrangement = if (isRangeMode) Arrangement.Center else Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedVisibility(
            visible = !isRangeMode,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween()
            ) + fadeIn(animationSpec = tween()),
            exit = fadeOut(animationSpec = tween(0))
        ) {
            IconButton(onClick = onPreviousDayClick, enabled = !isLoading || isToday) {
                Icon(
                    modifier = Modifier.mirror(),
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.previous_day),
                )
            }
        }

        Row(modifier = Modifier
            .clickable(
                onClick = {
                    if (!isLoading || isToday) {
                        onDayClick()
                    }
                }
            ),
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier,
                text = if (isRangeMode && dateRangeStart != null && dateRangeEnd != null) "${
                    parseDateText(
                        date = dateRangeStart
                    )
                } - ${parseDateText(date = dateRangeEnd)}" else parseDateText(date = date),
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.width(spacing.spaceExtraSmall))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Change date / range"
            )
        }

        AnimatedVisibility(
            visible = !isRangeMode,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween()
            ) + fadeIn(animationSpec = tween()),
            exit = fadeOut(animationSpec = tween(0))
        ) {
            IconButton(onClick = onNextDayClick, enabled = (!isToday && !isLoading)) {
                Icon(
                    modifier = Modifier.mirror(),
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = stringResource(id = R.string.next_day)
                )
            }
        }
    }
}