package ly.com.tahaben.launcher_domain.use_case

data class LauncherUseCases(
    val getInstalledActivities: GetInstalledActivities,
    val checkIfCurrentLauncher: CheckIfCurrentLauncher,
    val openDefaultLauncherSettings: OpenDefaultLauncherSettings,
    val setBlackWallpaper: SetBlackWallpaper
)