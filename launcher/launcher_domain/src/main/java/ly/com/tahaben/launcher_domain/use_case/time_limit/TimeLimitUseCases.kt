package ly.com.tahaben.launcher_domain.use_case.time_limit

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 16,Feb,2023
 */
data class TimeLimitUseCases(
    val addTimeLimitToDb: AddTimeLimitToDb,
    val removeTimeLimitFromDb: RemoveTimeLimitFromDb,
    val getTimeLimitForPackage: GetTimeLimitForPackage,
    val addPackageToTimeLimitWhiteList: AddPackageToTimeLimitWhiteList,
    val removePackageFromTimeLimitWhiteList: RemovePackageFromTimeLimitWhiteList,
    val isPackageInTimeLimitWhiteList: IsPackageInTimeLimitWhiteList,
    val setTimeLimiterEnabled: SetTimeLimiterEnabled,
    val isTimeLimiterEnabled: IsTimeLimiterEnabled,
    val isAccessibilityPermissionGranted: IsAccessibilityPermissionGranted,
    val askForAccessibilityPermission: AskForAccessibilityPermission,
    val isAppearOnTopPermissionGranted: IsAppearOnTopPermissionGranted,
    val askForAppearOnTopPermission: AskForAppearOnTopPermission,
    val getInstalledApps: GetInstalledApps,
    val startTimeLimitService: StartTimeLimitService,
    val stopTimeLimitService: StopTimeLimitService,
)