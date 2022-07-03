package ly.com.tahaben.core.util

sealed class SearchEvent {
    data class OnQueryChange(val query: String) : SearchEvent()
    object OnSearch : SearchEvent()
    object HideSearch : SearchEvent()
    data class OnSearchFocusChange(val isFocused: Boolean) : SearchEvent()
    data class OnSystemAppsVisibilityChange(val showSystemApps: Boolean) : SearchEvent()
}
