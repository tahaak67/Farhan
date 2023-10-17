package ly.com.tahaben.usage_overview_presentation.components

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.CategoryBarColor
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.usage_overview_presentation.UsageOverviewState
import java.text.DecimalFormat


@Composable
fun UsageOverviewHeader(
    state: UsageOverviewState,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val decimalFormat = DecimalFormat.getInstance()
    val animatedHoursCount = animateIntAsState(
        targetValue = state.totalUsageDuration,
        label = "hours count",
    )
    val animatedMinutesCount = animateIntAsState(
        targetValue = state.totalUsageMinutes,
        label = "minutes count"
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .padding(spacing.spaceMedium)
                .clip(RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Row(
                modifier = Modifier
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(spacing.spaceMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.clock_usage),
                    contentDescription = stringResource(R.string.clock_icon)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.total_usage),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.75f
                        ),
                        textAlign = TextAlign.Start
                    )
                    Row {
                        Row {
                            Crossfade(
                                targetState = state.isLoading,
                                label = "cross hours -"
                            ) { isLoading ->
                                if (isLoading) {
                                    Text(
                                        text = "-",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 40.sp
                                    )
                                } else {
                                    AnimatedVisibility(visible = (state.totalUsageDuration > 0)) {
                                        Text(
                                            text = decimalFormat.format(animatedHoursCount.value),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 40.sp
                                        )
                                    }
                                }
                            }
                            AnimatedVisibility(visible = (state.totalUsageDuration > 0)) {
                                Text(
                                    text = stringResource(id = R.string.hours),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 40.sp
                                )
                            }


                        }
                        Spacer(modifier = Modifier.width(spacing.spaceExtraSmall))
                        Row {
                            Crossfade(
                                targetState = state.isLoading,
                                label = "cross minues -"
                            ) { isLoading ->
                                if (isLoading) {
                                    Text(
                                        text = "-",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 40.sp
                                    )
                                } else {
                                    AnimatedVisibility(visible = (state.totalUsageMinutes > 0)) {
                                        Text(
                                            text = decimalFormat.format(animatedMinutesCount.value),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 40.sp
                                        )
                                    }

                                }
                            }
                            AnimatedVisibility(visible = (state.totalUsageMinutes > 0)) {
                                Text(
                                    text = stringResource(id = R.string.minutes),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 40.sp
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(spacing.spaceSmall))
        Spacer(modifier = Modifier.height(spacing.spaceExtraLarge))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                UsageBarInfo(
                    value = state.totalSocialUsageMilli,
                    total = state.totalUsageMilli,
                    name = stringResource(id = R.string.category_social),
                    color = CategoryBarColor,
                    modifier = Modifier.size(90.dp)
                )
                UsageBarInfo(
                    value = state.totalProductivityUsageMilli,
                    total = state.totalUsageMilli,
                    name = stringResource(id = R.string.category_productivity),
                    color = CategoryBarColor,
                    modifier = Modifier.size(90.dp)
                )
                UsageBarInfo(
                    value = state.totalGameUsageMilli,
                    total = state.totalUsageMilli,
                    name = stringResource(id = R.string.category_game),
                    color = CategoryBarColor,
                    modifier = Modifier.size(90.dp)
                )
            }
        }
    }
}