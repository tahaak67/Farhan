package ly.com.tahaben.usage_overview_presentation.components

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ly.com.tahaben.core.R
import ly.com.tahaben.core_ui.FatColor
import ly.com.tahaben.core_ui.LocalSpacing
import ly.com.tahaben.core_ui.Page
import ly.com.tahaben.core_ui.ProteinColor
import ly.com.tahaben.usage_overview_presentation.UsageOverviewState


@Composable
fun UsageOverviewHeader(
    state: UsageOverviewState,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val animatedHoursCount = animateIntAsState(
        targetValue = state.totalUsageDuration,
    )
    val animatedMinutesCount = animateIntAsState(
        targetValue = state.totalUsageMinutes
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomStart = 50.dp,
                    bottomEnd = 50.dp
                )
            )
            .background(Page)
            .padding(
                horizontal = spacing.spaceLarge,
                vertical = spacing.spaceMedium
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
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
                                        text = animatedHoursCount.value.toString(),
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
                                        text = animatedMinutesCount.value.toString(),
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
                    color = ProteinColor,
                    modifier = Modifier.size(90.dp)
                )
                UsageBarInfo(
                    value = state.totalProductivityUsageMilli,
                    total = state.totalUsageMilli,
                    name = stringResource(id = R.string.category_productivity),
                    color = ProteinColor,
                    modifier = Modifier.size(90.dp)
                )
                UsageBarInfo(
                    value = state.totalGameUsageMilli,
                    total = state.totalUsageMilli,
                    name = stringResource(id = R.string.category_game),
                    color = FatColor,
                    modifier = Modifier.size(90.dp)
                )
            }
        }
    }
}