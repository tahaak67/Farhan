package ly.com.tahaben.notification_filter_presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.com.tahaben.notification_filter_domain.model.NotificationItem
import ly.com.tahaben.notification_filter_domain.preferences.Preferences
import ly.com.tahaben.notification_filter_domain.use_cases.NotificationFilterUseCases
import ly.com.tahaben.notification_filter_domain.util.ServiceUtil
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NotificationFilterViewModel @Inject constructor(
    private val notificationFilterUseCases: NotificationFilterUseCases,
    private val sharedPref: Preferences,
    private val serviceUtil: ServiceUtil
) : ViewModel() {


    var state by mutableStateOf(NotificationFilterState())
        private set

    private var getNotificationsJob: Job? = null

    // Full, unfiltered list straight from the DB. Kept as the source of truth so the
    // in-memory search can re-filter without re-querying Room.
    private var allNotifications: List<NotificationItem> = emptyList()

    init {
        checkServiceStats()
        getNotifications()
        isFirstTimeOpened()
    }

    fun checkServiceStats() {
        viewModelScope.launch {
            state = state.copy(
                isServiceEnabled = notificationFilterUseCases
                    .checkIfNotificationServiceIsEnabled()
            )
        }
        checkIfNotificationAccessGranted()
    }

    private fun checkIfNotificationAccessGranted() {
        state = state.copy(
            isPermissionGranted = notificationFilterUseCases.checkIfNotificationAccessIsGranted()
        )
    }

    fun startNotificationService() {
        notificationFilterUseCases.startNotificationService()
        setServiceEnabled()
    }

    private fun getNotifications() {
        getNotificationsJob?.cancel()
        getNotificationsJob =
            notificationFilterUseCases.getNotificationsFromDB()
                .onEach { notifications ->
                    allNotifications = notifications
                    state = state.copy(
                        isServiceEnabled = true,
                        filteredNotifications = filterBy(state.searchQuery)
                    )
                }
                .launchIn(viewModelScope)

    }

    // Pure in-memory filter over the already-loaded list. Matches the query (case
    // insensitive) against the app name, title, text and package name so users can find
    // a notification by what it says, not just which app sent it. Blank query => everything.
    private fun filterBy(query: String): List<NotificationItem> {
        val q = query.trim()
        if (q.isEmpty()) return allNotifications
        return allNotifications.filter { it.matches(q) }
    }

    private fun NotificationItem.matches(query: String): Boolean =
        appName?.contains(query, ignoreCase = true) == true ||
                title?.contains(query, ignoreCase = true) == true ||
                text?.contains(query, ignoreCase = true) == true ||
                packageName.contains(query, ignoreCase = true)

    fun onEvent(event: NotificationFilterEvent) {
        when (event) {
            is NotificationFilterEvent.OnOpenNotification -> {
                Timber.d("notification click viewmodel")
                notificationFilterUseCases.openNotification(
                    event.notificationItem.id,
                    event.notificationItem.packageName
                )
                deleteNotificationData(event.notificationItem)
            }
            is NotificationFilterEvent.OnDismissNotification -> {
                Timber.d("dismissed: ${event.notificationItem}")
                deleteNotificationData(event.notificationItem)
            }
            is NotificationFilterEvent.OnDeleteAllNotifications -> {
                Timber.d("delete all notifications")
                deleteAllNotifications()
            }

            is NotificationFilterEvent.OnExcludeAppClick -> {
                notificationFilterUseCases.addPackageToNotificationException(event.appPackageName)
            }

            is NotificationFilterEvent.OnLaunchAppInfoClick -> {
                serviceUtil.launchAppInfo(event.appPackageName)
            }

            is NotificationFilterEvent.OnToggleSearch -> {
                val active = !state.isSearchActive
                // Closing search clears the query and restores the full list.
                state = state.copy(
                    isSearchActive = active,
                    searchQuery = "",
                    filteredNotifications = allNotifications
                )
            }

            is NotificationFilterEvent.OnSearchQueryChange -> {
                state = state.copy(
                    searchQuery = event.query,
                    filteredNotifications = filterBy(event.query)
                )
            }
        }
    }

    private fun deleteNotificationData(notificationItem: NotificationItem) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                notificationFilterUseCases.deleteNotificationFromDB(
                    notificationItem
                )
                notificationFilterUseCases.deleteNotificationIntentFromHashmap(
                    notificationItem.id
                )
            }
        }
    }

    private fun deleteAllNotifications() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                notificationFilterUseCases.deleteAllNotifications()
            }
        }
    }

    private fun setServiceEnabled() {
        notificationFilterUseCases.setServiceState(true)
        state = state.copy(
            isServiceEnabled = true
        )
    }

    fun isFirstTimeOpened() {
        state = state.copy(
            isFirstTimeOpened = sharedPref.loadShouldShowcase()
        )
        Timber.d("sholdShowcase ${state.isFirstTimeOpened}")
    }

    fun setShowcased() {
        sharedPref.saveShouldShowcase(false)
        state = state.copy(
            isFirstTimeOpened = false
        )
    }
}