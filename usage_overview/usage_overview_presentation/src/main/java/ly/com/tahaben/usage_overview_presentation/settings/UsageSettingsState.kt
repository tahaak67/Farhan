package ly.com.tahaben.usage_overview_presentation.settings

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 06,May,2023
 */
data class UsageSettingsState(
    val isCacheEnabled: Boolean = false,
    val isAutoCachingEnabled: Boolean = false,
    val visiblePermissionDialogQueue: MutableList<String> = mutableListOf(),
    val isWeeklyReportsEnabled: Boolean = false,
    val isMonthlyReportsEnabled: Boolean = false,
    val isYearlyReportsEnabled: Boolean = false,
    val showSelectReportsDialog: Boolean = false,
    val isBackgroundWorkRestricted: Boolean = false,
    val isIgnoreLauncher: Boolean = false,
    val isIgnoreFarhan: Boolean = false
)
