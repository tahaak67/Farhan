package ly.com.tahaben.usage_overview_presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier


@Composable
fun CategoriesBar(
    /*totalSocialUsageHours: Int,
    totalSocialUsageMinutes: Int,
    totalProductivityUsageHours: Int,
    totalProductivityUsageMinutes: Int,
    totalUsageHours: Int,
    totalUsageMinutes: Int,*/
    modifier: Modifier = Modifier
) {
    val background = MaterialTheme.colors.background
    val caloriesExceedColor = MaterialTheme.colors.error
    val carbWidthRatio = remember {
        Animatable(0f)
    }
    val proteinWidthRatio = remember {
        Animatable(0f)
    }
    val fatWidthRatio = remember {
        Animatable(0f)
    }
    /*LaunchedEffect(key1 = totalSocialUsageHours) {
        carbWidthRatio.animateTo(
            targetValue = ((totalSocialUsageHours/ totalUsageHours).toFloat())
        )
    }
    LaunchedEffect(key1 = totalProductivityUsageHours) {
        proteinWidthRatio.animateTo(
            targetValue = ((totalProductivityUsageHours/ totalUsageHours).toFloat())
        )
    }
   *//* LaunchedEffect(key1 = total) {
        fatWidthRatio.animateTo(
            targetValue = ((fat * 9f) / calorieGoal)
        )
    }*//*
    Canvas(modifier = modifier) {
        if(totalSocialUsageHours <= totalUsageMinutes) {
            val carbsWidth = carbWidthRatio.value * size.width
            val proteinWidth = proteinWidthRatio.value * size.width
            val fatWidth = fatWidthRatio.value * size.width
            drawRoundRect(
                color = background,
                size = size,
                cornerRadius = CornerRadius(100f)
            )
            drawRoundRect(
                color = Black,
                size = Size(
                    width = carbsWidth + proteinWidth + fatWidth,
                    height = size.height
                ),
                cornerRadius = CornerRadius(100f)
            )
            drawRoundRect(
                color = Black,
                size = Size(
                    width = carbsWidth + proteinWidth,
                    height = size.height
                ),
                cornerRadius = CornerRadius(100f)
            )
            drawRoundRect(
                color = Black,
                size = Size(
                    width = carbsWidth,
                    height = size.height
                ),
                cornerRadius = CornerRadius(100f)
            )
        } else {
            drawRoundRect(
                color = caloriesExceedColor,
                size = size,
                cornerRadius = CornerRadius(100f)
            )
        }
    }*/
}