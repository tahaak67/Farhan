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
    object OnShowDateBottomSheet : UsageOverviewEvent()
    object OnDismissDateBottomSheet : UsageOverviewEvent()
    object OnSelectDateClick : UsageOverviewEvent()
    object OnSelectRangeClick : UsageOverviewEvent()
    object OnShowDatePickerDialog : UsageOverviewEvent()
    object OnDismissDatePickerDialog : UsageOverviewEvent()
    object OnShowRangePickerDialog : UsageOverviewEvent()
    object OnDismissRangePickerDialog : UsageOverviewEvent()
    data class OnDateSelected(val selectedDateMillis: Long) : UsageOverviewEvent()
    object OnShowHowDialog : UsageOverviewEvent()
    object OnDismissHowDialog : UsageOverviewEvent()
}
