package ly.com.tahaben.usage_overview_presentation


sealed class UsageOverviewEvent {
    object OnNextDayClick : UsageOverviewEvent()
    object OnPreviousDayClick : UsageOverviewEvent()
    // data class OnToggleMealClick(val meal: Meal): UsageOverviewEvent()
    // data class OnDeleteTrackedFoodClick(val trackedFood: TrackedFood): UsageOverviewEvent()
}
