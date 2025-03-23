package ly.com.tahaben.launcher_presentation.wait

sealed interface MindfulLaunchEvent {
    data class OnMindfulLaunchEnabled(val enabled: Boolean) : MindfulLaunchEvent
    data class OnSearchQueryChange(val query: String): MindfulLaunchEvent
    object OnSearch: MindfulLaunchEvent
    data class OnShowSystemAppsChange(val showSystemApps: Boolean): MindfulLaunchEvent
    data class OnShowWhiteListOnlyChange(val showWhiteListOnly: Boolean): MindfulLaunchEvent
    data class OnAddToWhiteList(val packageName: String): MindfulLaunchEvent
    data class OnRemoveFromWhiteList(val packageName: String): MindfulLaunchEvent
    data object ScreenShown: MindfulLaunchEvent
}