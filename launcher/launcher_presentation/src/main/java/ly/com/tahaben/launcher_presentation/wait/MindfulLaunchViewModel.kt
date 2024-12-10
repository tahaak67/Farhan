package ly.com.tahaben.launcher_presentation.wait

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.com.tahaben.core.data.repository.InstalledAppsRepository
import ly.com.tahaben.core.model.AppItem
import ly.com.tahaben.launcher_domain.preferences.Preference
import ly.com.tahaben.launcher_domain.use_case.time_limit.TimeLimitUseCases
import javax.inject.Inject

@HiltViewModel
class MindfulLaunchViewModel @Inject constructor(
    private val useCases: TimeLimitUseCases,
    private val preference: Preference,
    private val installedAppsRepository: InstalledAppsRepository
) : ViewModel() {

    private var _state = MutableStateFlow(MindfulLaunchState())
    val state: StateFlow<MindfulLaunchState> = _state
        .onStart {
            checkIfAccessibilityEnabled()
            getInstalledApps()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MindfulLaunchState()
        )

    /*private var _settingsState = MutableStateFlow(MindfulLaunchState())
    val settingsState: StateFlow<MindfulLaunchState> = _settingsState
        .onStart {

        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MindfulLaunchState()
        )*/

    fun onEvent(event: MindfulLaunchEvent) {
        when (event) {
            MindfulLaunchEvent.OnSearch -> {
                executeSearch()
            }

            is MindfulLaunchEvent.OnSearchQueryChange -> {
                _state.update {
                    it.copy(
                        searchQuery = event.query
                    )
                }
            }

            is MindfulLaunchEvent.OnShowSystemAppsChange -> {
                _state.update {
                    it.copy(isShowSystemApps = event.showSystemApps)
                }
            }

            is MindfulLaunchEvent.OnShowWhiteListOnlyChange -> {
                _state.update {
                    it.copy(isShowWhiteListOnly = event.showWhiteListOnly)
                }
            }

            is MindfulLaunchEvent.OnMindfulLaunchEnabled -> {
                if (event.enabled) {
                    checkIfAccessibilityEnabled()
                }
                _state.update {
                    it.copy(isMindfulLaunchEnabled = event.enabled)
                }
            }
            is MindfulLaunchEvent.OnAddToWhiteList -> {
                viewModelScope.launch {
                    preference.addPackageToMLWhiteList(event.packageName)
                }
            }
            is MindfulLaunchEvent.OnRemoveFromWhiteList -> {
                viewModelScope.launch {
                    preference.removePackageFromMLWhiteList(event.packageName)
                }
            }
        }
    }

    fun checkIfAccessibilityEnabled() {
        _state.update {
            it.copy(
                isAccessibilityPermissionGranted = useCases.isAccessibilityPermissionGranted()
            )
        }
    }

    private fun getInstalledApps() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            val exceptions = preference.getAppsInMLWhiteList()
            withContext(Dispatchers.IO) {
                val apps = installedAppsRepository.getInstalledApps()
                    .sortedBy { it.name }
                apps.forEach {
                    it.isException =
                        preference.isPackageInMLWhiteList(it.packageName)
                }
                val notInstalledPackages =
                    exceptions.filterNot { packageName -> packageName in apps.map { it.packageName } }
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

    private fun executeSearch() {
        val l = state.value.appsList.filter {
            it.name?.contains(state.value.searchQuery, true) == true
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
                searchResults = if (currentState.isShowSystemApps) {
                    currentState.appsList
                } else {
                    currentState.appsList.filter {
                        !it.isSystemApp
                    }
                }
            )
        }
        if (state.value.isShowWhiteListOnly) {
            filterWhitelistOnly()
        }
    }

    private fun filterWhitelistOnly() {
        if (state.value.isShowWhiteListOnly) {
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
