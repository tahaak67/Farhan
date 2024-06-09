package ly.com.tahaben.notification_filter_presentation.settings.exceptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.com.tahaben.core.model.AppItem
import ly.com.tahaben.core.util.SearchEvent
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.notification_filter_domain.preferences.Preferences
import ly.com.tahaben.notification_filter_domain.use_cases.NotificationFilterUseCases
import javax.inject.Inject

@HiltViewModel
class NotificationExceptionsViewModel @Inject constructor(
    private val notificationFilterUseCases: NotificationFilterUseCases,
    private val preferences: Preferences
) : ViewModel() {

    private val _state =
        MutableStateFlow(NotificationFilterExceptionsState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        getInstalledApps()
    }

    private fun getInstalledApps() {
        viewModelScope.launch {
            val exceptions = preferences.getNotificationFilterExceptionsList()
            withContext(Dispatchers.IO) {
                val apps = notificationFilterUseCases.getInstalledAppsList()
                    .sortedBy { it.name }
                apps.forEach {
                    it.isException =
                        notificationFilterUseCases.isPackageInNotificationException(it.packageName)
                }
                val notInstalledPackages = exceptions.filterNot { packageName -> packageName in apps.map { it.packageName } }
                val notInstalledApps = notInstalledPackages.map {
                    AppItem(name = it, packageName = it, isException = true)
                }
                val allApps = apps + notInstalledApps
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        appsList = allApps
                    )
                }
            }
            filterSystemApps()
        }
    }

    private fun refreshAppsList() {
        val apps = state.value.searchResults
        apps.forEach {
            it.isException =
                notificationFilterUseCases.isPackageInNotificationException(it.packageName)
        }
        _state.update { currentState ->
            currentState.copy(
                searchResults = apps
            )
        }
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnQueryChange -> {
                _state.update { it.copy(query = event.query) }
                executeSearch()
            }

            is SearchEvent.OnSearch -> {
                executeSearch()
            }

            is SearchEvent.OnSearchFocusChange -> {
                _state.update {
                    it.copy(isHintVisible = !event.isFocused && it.query.isBlank())
                }
            }

            is SearchEvent.OnSystemAppsVisibilityChange -> {
                _state.update { it.copy(showSystemApps = event.showSystemApps) }
                filterSystemApps()
            }

            is SearchEvent.HideSearch -> {
                _state.update { it.copy(query = "") }
                filterSystemApps()
            }

            is SearchEvent.OnExceptionsOnlyChange -> {
                _state.update { it.copy(showExceptionsOnly = event.showExceptionsOnly) }
                filterExceptionsOnly()
            }
        }
    }

    private fun executeSearch() {
        val l = state.value.appsList.filter {
            it.name?.contains(state.value.query, true) == true
        }
        _state.update { currentState ->
            currentState.copy(
                searchResults = l
            )
        }
    }

    private fun filterSystemApps() {
        _state.update { currentState ->
            currentState.copy(
                searchResults = if (currentState.showSystemApps) {
                    currentState.appsList
                } else {
                    currentState.appsList.filter {
                        !it.isSystemApp
                    }
                }
            )
        }
        if (state.value.showExceptionsOnly) {
            filterExceptionsOnly()
        }
    }

    fun removeAppFromExceptions(packageName: String) {
        notificationFilterUseCases.removePackageFromNotificationException(packageName)
    }

    fun addAppToExceptions(packageName: String) {
        notificationFilterUseCases.addPackageToNotificationException(packageName)
    }

    private fun filterExceptionsOnly() {
        if (state.value.showExceptionsOnly) {
            _state.update { currentState ->
                currentState.copy(
                    searchResults = currentState.searchResults.filter {
                        it.isException
                    }
                )
            }
        } else {
            filterSystemApps()
        }
    }
}