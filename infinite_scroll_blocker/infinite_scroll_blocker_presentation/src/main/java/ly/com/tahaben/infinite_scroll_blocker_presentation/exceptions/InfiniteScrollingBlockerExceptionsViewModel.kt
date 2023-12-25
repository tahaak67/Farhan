package ly.com.tahaben.infinite_scroll_blocker_presentation.exceptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.com.tahaben.core.util.SearchEvent
import ly.com.tahaben.core.util.UiEvent
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.InfiniteScrollUseCases
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InfiniteScrollingBlockerExceptionsViewModel @Inject constructor(
    private val infiniteScrollUseCases: InfiniteScrollUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(InfiniteScrollExceptionsState())
    val state: StateFlow<InfiniteScrollExceptionsState> get() = _state

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    init {
        getInstalledApps()
    }


    private fun getInstalledApps() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val apps = infiniteScrollUseCases.getInstalledAppsList()
                    .sortedBy { it.name }
                apps.forEach {
                    it.isException =
                        infiniteScrollUseCases.isPackageInInfiniteScrollExceptions(it.packageName)
                }
                apps.forEach {
                    Timber.d("app: $it")
                }
                _state.update {
                    it.copy(
                        isLoading = false,
                        installedApps = apps
                    )
                }
            }
            filterSystemApps()
        }
    }

    private fun refreshAppsList() {
        val apps = _state.value.searchResults
        apps.forEach {
            it.isException =
                infiniteScrollUseCases.isPackageInInfiniteScrollExceptions(it.packageName)
        }
        _state.update {
            it.copy(
                searchResults = apps
            )
        }

    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnQueryChange -> {
                _state.update {
                    it.copy(query = event.query)
                }
                executeSearch()
            }

            is SearchEvent.OnSearch -> {
                executeSearch()
            }
            is SearchEvent.OnSearchFocusChange -> {
                _state.update {
                    it.copy(
                        isHintVisible = !event.isFocused && state.value.query.isBlank()
                    )
                }
            }
            is SearchEvent.OnSystemAppsVisibilityChange -> {
                _state.update {
                    it.copy(
                        showSystemApps = event.showSystemApps
                    )
                }

                filterSystemApps()
            }
            is SearchEvent.HideSearch -> {
                _state.update {
                    it.copy(query = "")
                }
                filterSystemApps()
            }
            is SearchEvent.OnExceptionsOnlyChange -> {
                _state.update {
                    it.copy(
                        showExceptionsOnly = event.showExceptionsOnly
                    )
                }
                filterExceptionsOnly()
            }
        }
    }

    private fun executeSearch() {
        val l = _state.value.installedApps.filter {
            it.name?.contains(state.value.query, true) == true
        }
        Timber.d("search query: ${state.value.query} \n l: $l")
        Timber.d("search list: ${state.value.searchResults} \n l: $l")
        Timber.d("apps list: ${state.value.installedApps} \n l: $l")
        _state.update {
            it.copy(
                searchResults = l
            )
        }
    }

    private fun filterSystemApps() {
        _state.update {
            it.copy(
                searchResults = if (state.value.showSystemApps) {
                    state.value.installedApps
                } else {
                    state.value.installedApps.filter {
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
        infiniteScrollUseCases.removePackageFromInfiniteScrollExceptions(packageName)
    }

    fun addAppToExceptions(packageName: String) {
        infiniteScrollUseCases.addPackageToInfiniteScrollExceptions(packageName)
    }

    private fun filterExceptionsOnly() {
        if (_state.value.showExceptionsOnly) {
            _state.update {
                it.copy(
                    searchResults = state.value.searchResults.filter {
                        it.isException
                    }
                )
            }
        } else {
            filterSystemApps()
        }
    }
}