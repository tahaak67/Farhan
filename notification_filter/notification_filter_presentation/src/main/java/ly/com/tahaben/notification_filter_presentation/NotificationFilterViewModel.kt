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
import ly.com.tahaben.notification_filter_domain.use_cases.NotificationFilterUseCases
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NotificationFilterViewModel @Inject constructor(
    private val notificationFilterUseCases: NotificationFilterUseCases
) : ViewModel() {


    var state by mutableStateOf(NotificationFilterState())
        private set

    private var getNotificationsJob: Job? = null

    init {
        checkServiceStats()
        getNotifications()
    }

    fun checkServiceStats() {

        state = state.copy(
            isServiceEnabled = notificationFilterUseCases
                .checkIfNotificationServiceIsEnabled()
        )

    }

    fun startNotificationService() {
        notificationFilterUseCases.startNotificationService()
        setServiceEnabled()
    }

    fun getNotifications() {
        getNotificationsJob?.cancel()
        getNotificationsJob =
            notificationFilterUseCases.getNotificationsFromDB()
                .onEach { notifications ->
                    state = state.copy(
                        isServiceEnabled = true,
                        filteredNotifications = notifications
                    )
                }
                .launchIn(viewModelScope)

    }

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
}