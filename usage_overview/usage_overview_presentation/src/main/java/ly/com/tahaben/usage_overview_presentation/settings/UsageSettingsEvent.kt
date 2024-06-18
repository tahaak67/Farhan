package ly.com.tahaben.usage_overview_presentation.settings

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 13,May,2023
 */
sealed class UsageSettingsEvent {
    object ShowSelectReportsDialog : UsageSettingsEvent()
    object DismissSelectReportsDialog : UsageSettingsEvent()
    object DismissPermissionDialog : UsageSettingsEvent()
    data class OnIgnoreLauncherClick(val ignoreLauncher: Boolean) : UsageSettingsEvent()
    data class OnIgnoreFarhanClick(val ignoreFarhan: Boolean) : UsageSettingsEvent()
}
