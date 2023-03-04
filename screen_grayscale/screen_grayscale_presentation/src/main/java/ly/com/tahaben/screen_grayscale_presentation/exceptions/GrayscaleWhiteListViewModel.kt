package ly.com.tahaben.screen_grayscale_presentation.exceptions

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
import ly.com.tahaben.screen_grayscale_domain.use_cases.GrayscaleUseCases
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GrayscaleWhiteListViewModel @Inject constructor(
    private val grayscaleUseCases: GrayscaleUseCases
) : ViewModel() {


    var state by mutableStateOf(GrayscaleWhiteListState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    init {
        getInstalledApps()
    }


    private fun getInstalledApps() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val apps = grayscaleUseCases.getInstalledAppsList()
                    .sortedBy { it.name }
                apps.forEach {
                    it.isException =
                        grayscaleUseCases.isPackageInGrayscaleWhiteList(it.packageName)
                }
                apps.forEach {
                    Timber.d("app: $it")
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
                grayscaleUseCases.isPackageInGrayscaleWhiteList(it.packageName)
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
                    showWhitelistOnly = event.showExceptionsOnly
                )
                filterWhitelistOnly()
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
        if (state.showWhitelistOnly) {
            filterWhitelistOnly()
        }
    }

    fun removeAppFromWhiteList(packageName: String) {
        grayscaleUseCases.removePackageFromGrayscaleWhiteList(packageName)
    }

    fun addAppToWhiteList(packageName: String) {
        grayscaleUseCases.addPackageToGrayscaleWhiteList(packageName)
    }

    private fun filterWhitelistOnly() {
        if (state.showWhitelistOnly) {
            state = state.copy(
                searchResults = state.searchResults.filter {
                    it.isException
                }
            )
        } else {
            filterSystemApps()
        }
    }
}