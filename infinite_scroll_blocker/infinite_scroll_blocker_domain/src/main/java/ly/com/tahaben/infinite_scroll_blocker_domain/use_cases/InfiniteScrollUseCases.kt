package ly.com.tahaben.infinite_scroll_blocker_domain.use_cases

data class InfiniteScrollUseCases(
    val addPackageToInfiniteScrollExceptions: AddPackageToInfiniteScrollExceptions,
    val askForAccessibilityPermission: AskForAccessibilityPermission,
    val askForAppearOnTopPermission: AskForAppearOnTopPermission,
    val getInfiniteScrollExceptions: GetInfiniteScrollExceptions,
    val isPackageInInfiniteScrollExceptions: IsPackageInInfiniteScrollExceptions,
    val isServiceEnabled: IsServiceEnabled,
    val isAccessibilityPermissionGranted: IsAccessibilityPermissionGranted,
    val isAppearOnTopPermissionGranted: IsAppearOnTopPermissionGranted,
    val removePackageFromInfiniteScrollExceptions: RemovePackageFromInfiniteScrollExceptions,
    val setServiceState: SetServiceState,
    val setTimeOutDuration: SetTimeOutDuration,
    val getTimeOutDuration: GetTimeOutDuration,
    val getInstalledAppsList: GetInstalledAppsList,
    val saveShouldShowOnBoarding: SaveShouldShowOnBoarding,
    val loadShouldShowOnBoarding: LoadShouldShowOnBoarding,
    val getCountDown: GetCountDown,
    val getDialogMessage: GetDialogMessage
)
