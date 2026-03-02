package ly.com.tahaben.launcher_presentation.wait

sealed interface DelayedLaunchEvent {
    data class OnDelayedLaunchEnabled(val enabled: Boolean) : DelayedLaunchEvent
    data class OnSearchQueryChange(val query: String): DelayedLaunchEvent
    object OnSearch: DelayedLaunchEvent
    data class OnShowSystemAppsChange(val showSystemApps: Boolean): DelayedLaunchEvent
    data class OnShowWhiteListOnlyChange(val showWhiteListOnly: Boolean): DelayedLaunchEvent
    data class OnAddToWhiteList(val packageName: String): DelayedLaunchEvent
    data class OnRemoveFromWhiteList(val packageName: String): DelayedLaunchEvent
    data object ScreenShown: DelayedLaunchEvent
    data class OnSetDelayDuration(val durationInSeconds: Int): DelayedLaunchEvent
    data class AddMsgToDelayMessages(val msg: String): DelayedLaunchEvent
    data class SetDelayMsg(val msg: String):DelayedLaunchEvent
    data class DeleteDelayMsg(val msg: String): DelayedLaunchEvent
    data object ResetDelayMessages: DelayedLaunchEvent
}