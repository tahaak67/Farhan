package ly.com.tahaben.launcher_presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.com.tahaben.core.util.SearchEvent
import ly.com.tahaben.launcher_domain.use_case.LauncherUseCases
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val launcherUseCases: LauncherUseCases
) : ViewModel() {

    var state by mutableStateOf(LauncherState())
        private set

    init {
        loadApps()
    }

    fun loadApps() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val allApps = launcherUseCases.getInstalledActivities()
                    .sortedBy { it.name }
                withContext(Dispatchers.Main) {
                    state = state.copy(
                        appsList = allApps,
                        searchResults = allApps,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnQueryChange -> {
                state = state.copy(query = event.query)
                if (event.query.trim().isBlank()) {
                    state = state.copy(
                        searchResults = state.appsList
                    )
                } else {
                    executeSearch()
                }

            }

            is SearchEvent.OnSearch -> {
                executeSearch()
            }
            is SearchEvent.OnSearchFocusChange -> {
                state = state.copy(
                    isHintVisible = !event.isFocused
                )
            }
            //not literally hidding search in this one, just clearing text and hiding keyboard
            is SearchEvent.HideSearch -> {
                state = state.copy(
                    query = "",
                    searchResults = state.appsList
                )
            }
            else -> {}
        }
    }

    private fun executeSearch() {
        val searchResults = state.appsList.filter {
            it.name?.contains(state.query, true) == true
        }

        state = state.copy(
            searchResults = searchResults
        )
    }
}