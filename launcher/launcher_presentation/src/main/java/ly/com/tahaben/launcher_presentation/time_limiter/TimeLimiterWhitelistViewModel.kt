package ly.com.tahaben.launcher_presentation.time_limiter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.com.tahaben.core.util.SearchEvent
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.launcher_domain.use_case.time_limit.TimeLimitUseCases
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TimeLimiterWhitelistViewModel @Inject constructor(
    private val timeLimitUseCases: TimeLimitUseCases
) : ViewModel() {

    var state by mutableStateOf(TimeLimiterWhitelistState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        getInstalledApps()
    }


    private fun getInstalledApps() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val apps = timeLimitUseCases.getInstalledApps()
                    .sortedBy { it.name }
                apps.forEach {
                    it.isException =
                        timeLimitUseCases.isPackageInTimeLimitWhiteList(it.packageName)
                }
                state = state.copy(
                    isLoading = false,
                    installedApps = apps
                )
            }
            filterSystemApps()
        }
    }

    private fun refreshAppsList() {
        val apps = state.searchResults
        apps.forEach {
            it.isException =
                timeLimitUseCases.isPackageInTimeLimitWhiteList(it.packageName)
        }
        state = state.copy(
            searchResults = apps
        )
    }


    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnQueryChange -> {
                state = state.copy(query = event.query)
                executeSearch()
            }

            is SearchEvent.OnSearch -> {
                executeSearch()
            }
            is SearchEvent.OnSearchFocusChange -> {
                state = state.copy(
                    isHintVisible = !event.isFocused && state.query.isBlank()
                )
            }
            is SearchEvent.OnSystemAppsVisibilityChange -> {
                state = state.copy(
                    showSystemApps = event.showSystemApps
                )
                filterSystemApps()
            }
            is SearchEvent.HideSearch -> {
                state = state.copy(query = "")
                filterSystemApps()
            }
            is SearchEvent.OnExceptionsOnlyChange -> {
                state = state.copy(
                    isExceptionsOnly = event.showExceptionsOnly
                )
                filteredExceptionsOnly(event.showExceptionsOnly)
            }
        }
    }

    private fun executeSearch() {
        val l = state.installedApps.filter {
            it.name?.contains(state.query, true) == true
        }
        Timber.d("search query: ${state.query} \n l: $l")
        Timber.d("search list: ${state.searchResults} \n l: $l")
        Timber.d("apps list: ${state.installedApps} \n l: $l")

        state = state.copy(
            searchResults = l
        )
    }

    private fun filterSystemApps() {
        state = state.copy(
            searchResults = if (state.showSystemApps) {
                state.installedApps
            } else {
                state.installedApps.filter {
                    !it.isSystemApp
                }
            }
        )
    }

    private fun filteredExceptionsOnly(only: Boolean) {
        state = if (only){
            state.copy(
                searchResults = state.installedApps.filter { it.isException }
            )
        } else {
            state.copy(
                searchResults = state.installedApps
            )
        }
        executeSearch()
    }

    fun removeAppFromWhiteList(packageName: String) {
        timeLimitUseCases.removePackageFromTimeLimitWhiteList(packageName)
    }

    fun addAppToWhiteList(packageName: String) {
        timeLimitUseCases.addPackageToTimeLimitWhiteList(packageName)
    }
}