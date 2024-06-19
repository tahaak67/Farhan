package ly.com.tahaben.farhan.widget

import ly.com.tahaben.core.model.ThemeColors
import ly.com.tahaben.core.model.UIModeAppearance
import ly.com.tahaben.usage_overview_presentation.widget.UsageWidget
/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 12,Jun,2024
 */
data class WidgetPreviewState(
    val updateInterval: Int = 60,
    val isMenuExpanded: Boolean = false,
    val isTextSizeMenuExpanded: Boolean = false,
    val themeColors: ThemeColors = ThemeColors.Classic,
    val style: UsageWidget.Style = UsageWidget.Style.VERTICAL,
    val compareWithYesterday: Boolean = false,
    val uiModeAppearance: UIModeAppearance = UIModeAppearance.FOLLOW_SYSTEM,
    val textSize: Int = 32
)
