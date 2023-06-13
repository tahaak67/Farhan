package ly.com.tahaben.usage_overview_presentation.components

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
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
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.CategoryBarColor
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.Page
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
    )
    val animatedMinutesCount = animateIntAsState(
        targetValue = state.totalUsageMinutes
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .padding(spacing.spaceMedium)
                .clip(RoundedCornerShape(24.dp)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Page)
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.total_usage),
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.primaryVariant,
                    textAlign = TextAlign.Start
                )
                Row {
                    Row {
                        Crossfade(targetState = state.isLoading) { isLoading ->
                            if (isLoading) {
                                Text(
                                    text = "-",
                                    style = MaterialTheme.typography.body1,
                                    color = MaterialTheme.colors.primaryVariant,
                                    fontSize = 40.sp
                                )
                            } else {
                                AnimatedVisibility(visible = (state.totalUsageDuration > 0)) {
                                    Text(
                                        text = decimalFormat.format(animatedHoursCount.value),
                                        style = MaterialTheme.typography.body1,
                                        color = MaterialTheme.colors.primaryVariant,
                                        fontSize = 40.sp
                                    )
                                }
                            }
                        }
                        AnimatedVisibility(visible = (state.totalUsageDuration > 0)) {
                            Text(
                                text = stringResource(id = R.string.hours),
                                style = MaterialTheme.typography.body1,
                                color = MaterialTheme.colors.primaryVariant,
                                fontSize = 40.sp
                            )
                        }


                    }
                    Spacer(modifier = Modifier.width(spacing.spaceExtraSmall))
                    Row {
                        Crossfade(targetState = state.isLoading) { isLoading ->
                            if (isLoading) {
                                Text(
                                    text = "-",
                                    style = MaterialTheme.typography.body1,
                                    color = MaterialTheme.colors.primaryVariant,
                                    fontSize = 40.sp
                                )
                            } else {
                                AnimatedVisibility(visible = (state.totalUsageMinutes > 0)) {
                                    Text(
                                        text = decimalFormat.format(animatedMinutesCount.value),
                                        style = MaterialTheme.typography.body1,
                                        color = MaterialTheme.colors.primaryVariant,
                                        fontSize = 40.sp
                                    )
                                }

                            }
                        }
                        AnimatedVisibility(visible = (state.totalUsageMinutes > 0)) {
                            Text(
                                text = stringResource(id = R.string.minutes),
                                style = MaterialTheme.typography.body1,
                                color = MaterialTheme.colors.primaryVariant,
                                fontSize = 40.sp
                            )
                        }
                    }

                }
            }
        }
        Spacer(modifier = Modifier.height(spacing.spaceSmall))
        Spacer(modifier = Modifier.height(spacing.spaceLarge))
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