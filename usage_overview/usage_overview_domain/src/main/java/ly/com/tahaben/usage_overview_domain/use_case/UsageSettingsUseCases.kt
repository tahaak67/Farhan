package ly.com.tahaben.usage_overview_domain.use_case

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 15,May,2023
 */
data class UsageSettingsUseCases(
    val setAutoCachingEnabled: SetAutoCachingEnabled,
    val setUsageReportsEnabled: SetUsageReportsEnabled,
    val getEnabledUsageReports: GetEnabledUsageReports,
    val isAutoCachingEnabled: IsAutoCachingEnabled,
    val isCachingEnabled: IsCachingEnabled,
    val setCachingEnabled: SetCachingEnabled,
    val openAppSettings: OpenAppSettings,
)
