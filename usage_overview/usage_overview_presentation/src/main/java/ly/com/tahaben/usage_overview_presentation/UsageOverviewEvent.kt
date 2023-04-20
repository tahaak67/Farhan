package ly.com.tahaben.usage_overview_presentation


sealed class UsageOverviewEvent {
    object OnNextDayClick : UsageOverviewEvent()
    object OnPreviousDayClick : UsageOverviewEvent()

    data class OnRangeSelected(val startDateMillis: Long?, val endDateMillis: Long?) :
        UsageOverviewEvent()

    object OnDisableRangeMode : UsageOverviewEvent()
    object OnShowDropDown : UsageOverviewEvent()
    object OnDismissDropDown : UsageOverviewEvent()
    object OnShowConfirmDeleteDialog : UsageOverviewEvent()
    object OnDismissConfirmDeleteDialog : UsageOverviewEvent()
    object OnDeleteCacheForDay : UsageOverviewEvent()
}
