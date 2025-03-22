package ly.com.tahaben.launcher_presentation.wait

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.com.tahaben.core.data.repository.InstalledAppsRepository
import ly.com.tahaben.core.model.AppItem
import ly.com.tahaben.core.service.AccessibilityServiceUtils
import ly.com.tahaben.launcher_domain.preferences.Preference
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DelayedLaunchViewModel @Inject constructor(
    private val accessibilityUtils: AccessibilityServiceUtils,
    private val preference: Preference,
    private val installedAppsRepository: InstalledAppsRepository
) : ViewModel() {

    private var _state = MutableStateFlow(DelayedLaunchState())
    val state = _state.asStateFlow()
        .onStart {
//            checkIfAccessibilityEnabled()
            getInstalledApps()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DelayedLaunchState()
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
    init {
        viewModelScope.launch {
            preference.isDelayedLaunchEnabled().collectLatest { isEnabled ->
                _state.update {
                    it.copy(isMindfulLaunchEnabled = isEnabled)
                }
            }
        }
    }

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
                executeSearch()
            }

            is MindfulLaunchEvent.OnShowSystemAppsChange -> {
                _state.update {
                    it.copy(isShowSystemApps = event.showSystemApps)
                }
                filterSystemApps()
            }

            is MindfulLaunchEvent.OnShowWhiteListOnlyChange -> {
                _state.update {
                    it.copy(isShowWhiteListOnly = event.showWhiteListOnly)
                }
                filterWhitelistOnly()
            }

            is MindfulLaunchEvent.OnMindfulLaunchEnabled -> {
                if (event.enabled) {
                    checkIfAccessibilityEnabled()
                }
                viewModelScope.launch {
                    preference.setDelayedLaunchEnabled(event.enabled)
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
            MindfulLaunchEvent.ScreenShown -> {
                checkIfAccessibilityEnabled()
            }
        }
    }

    fun checkIfAccessibilityEnabled() {
        _state.update {
            it.copy(
                isAccessibilityPermissionGranted = accessibilityUtils.checkIfAccessibilityPermissionGranted()
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
            Timber.d("exceptions size ${exceptions.size}")
            withContext(Dispatchers.IO) {
                val apps = installedAppsRepository.getInstalledApps()
                    .sortedBy { it.name }
                apps.forEach {
                    it.isException =
                        preference.isPackageInMLWhiteList(it.packageName)
                }
                Timber.d("got apps size ${apps.size}")
                val notInstalledPackages =
                    exceptions.filterNot { packageName -> packageName in apps.map { it.packageName } }
                val notInstalledApps = notInstalledPackages.map {
                    AppItem(name = it, packageName = it, isException = true)
                }
                val allApps = apps + notInstalledApps
                Timber.d("got all apps size ${allApps.size}")
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        appsList = allApps
                    )
                }
                Timber.d("done")
            }
            /*_state.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    appsList = listOf(
                        AppItem("test","test",isException = true)
                    )
                )
            }*/
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
