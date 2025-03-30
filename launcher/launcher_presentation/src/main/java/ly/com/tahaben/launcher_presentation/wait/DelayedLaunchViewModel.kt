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
import ly.com.tahaben.launcher_domain.model.LaunchAttempt
import ly.com.tahaben.launcher_domain.preferences.Preference
import ly.com.tahaben.launcher_domain.repository.LaunchAttemptsRepository
import ly.com.tahaben.launcher_domain.repository.WorkerRepository
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

@HiltViewModel
class DelayedLaunchViewModel @Inject constructor(
    private val accessibilityUtils: AccessibilityServiceUtils,
    private val preference: Preference,
    private val installedAppsRepository: InstalledAppsRepository,
    private val launchAttemptsRepository: LaunchAttemptsRepository,
    private val workerRepository: WorkerRepository
) : ViewModel() {

    private var _state = MutableStateFlow(DelayedLaunchState())
    val state = _state.asStateFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DelayedLaunchState()
        )

    private var _whiteListState = MutableStateFlow(DelayedLaunchWhiteListState())
    val whiteListState = _whiteListState.asStateFlow()
        .onStart {
            getInstalledApps()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DelayedLaunchWhiteListState()
        )

    private var _launchAttemptState = MutableStateFlow(0)
    val launchAttemptCount = _launchAttemptState.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                preference.isDelayedLaunchEnabled().collectLatest { isEnabled ->
                    _state.update {
                        it.copy(isMindfulLaunchEnabled = isEnabled)
                    }
                }
            }
            launch {
                preference.getDelayedLaunchDuration().collectLatest { duration ->
                    _state.update {
                        it.copy(delayDurationSeconds = duration)
                    }
                }
            }
            launch {
                preference.getDelayedLaunchMessages().collectLatest { messages ->
                    Timber.d("delayed messages list: $messages")
                    _state.update {
                        it.copy(delayedLaunchMessages = messages)
                    }
                }
            }
            launch {
                preference.getDelayedLaunchMessage().collectLatest { message ->
                    _state.update {
                        it.copy(selectedDelayedLaunchMessage = message)
                    }
                }
            }
        }
    }

    fun onEvent(event: DelayedLaunchEvent) {
        when (event) {
            DelayedLaunchEvent.OnSearch -> {
                executeSearch()
            }

            is DelayedLaunchEvent.OnSearchQueryChange -> {
                _whiteListState.update {
                    it.copy(
                        searchQuery = event.query
                    )
                }
                executeSearch()
            }

            is DelayedLaunchEvent.OnShowSystemAppsChange -> {
                _whiteListState.update {
                    it.copy(isShowSystemApps = event.showSystemApps)
                }
                filterSystemApps()
            }

            is DelayedLaunchEvent.OnShowWhiteListOnlyChange -> {
                _whiteListState.update {
                    it.copy(isShowWhiteListOnly = event.showWhiteListOnly)
                }
                filterWhitelistOnly()
            }

            is DelayedLaunchEvent.OnDelayedLaunchEnabled -> {
                if (event.enabled) {
                    checkIfAccessibilityEnabled()
                    workerRepository.scheduleLaunchAttemptCleanupWork()
                }else{
                    workerRepository.cancelLaunchAttemptCleanupWork()
                }
                viewModelScope.launch {
                    preference.setDelayedLaunchEnabled(event.enabled)
                }
                _state.update {
                    it.copy(isMindfulLaunchEnabled = event.enabled)
                }
            }

            is DelayedLaunchEvent.OnAddToWhiteList -> {
                viewModelScope.launch {
                    preference.addPackageToMLWhiteList(event.packageName)
                }
            }

            is DelayedLaunchEvent.OnRemoveFromWhiteList -> {
                viewModelScope.launch {
                    preference.removePackageFromMLWhiteList(event.packageName)
                }
            }

            DelayedLaunchEvent.ScreenShown -> {
                checkIfAccessibilityEnabled()
            }

            is DelayedLaunchEvent.OnSetDelayDuration -> {
                viewModelScope.launch {
                    preference.setDelayedLaunchDuration(event.durationInSeconds)
                }
            }

            is DelayedLaunchEvent.AddMsgToDelayMessages -> {
                viewModelScope.launch {
                    preference.addDelayedLaunchMessage(event.msg)
                }
            }
            is DelayedLaunchEvent.DeleteDelayMsg -> {
                viewModelScope.launch {
                    preference.removeDelayedLaunchMessage(event.msg)
                }
            }
            is DelayedLaunchEvent.SetDelayMsg -> {
                viewModelScope.launch {
                    preference.setDelayedLaunchMessage(event.msg)
                }
            }

            DelayedLaunchEvent.ResetDelayMessages -> {
                viewModelScope.launch {
                    preference.resetDelayedLaunchMessages()
                }
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
            val exceptions = preference.getAppsInDelayedLaunchWhiteList()
            Timber.d("exceptions size ${exceptions.size}")
            withContext(Dispatchers.IO) {
                val apps = installedAppsRepository.getInstalledApps()
                    .sortedBy { it.name }
                apps.forEach {
                    it.isException =
                        preference.isPackageInDelayedLaunchWhiteList(it.packageName)
                }
                Timber.d("got apps size ${apps.size}")
                val notInstalledPackages =
                    exceptions.filterNot { packageName -> packageName in apps.map { it.packageName } }
                val notInstalledApps = notInstalledPackages.map {
                    AppItem(name = it, packageName = it, isException = true)
                }
                val allApps = apps + notInstalledApps
                Timber.d("got all apps size ${allApps.size}")
                _whiteListState.update { currentState ->
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
        val l = whiteListState.value.appsList.filter {
            it.name?.contains(whiteListState.value.searchQuery, true) == true
        }
        _whiteListState.update { currentState ->
            currentState.copy(
                searchResults = l
            )
        }
    }

    private fun filterSystemApps() {
        _whiteListState.update { currentState ->
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
        if (whiteListState.value.isShowWhiteListOnly) {
            filterWhitelistOnly()
        }
    }

    private fun filterWhitelistOnly() {
        if (whiteListState.value.isShowWhiteListOnly) {
            _whiteListState.update { currentState ->
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

    fun addLaunchAttempt(packageName: String) {
        viewModelScope.launch {
            launchAttemptsRepository.insert(
                LaunchAttempt(
                    id = 0,
                    packageName = packageName,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun getLaunchAttemptsForPackage(packageName: String) {
        val now = System.currentTimeMillis()
        val twentyFourHoursAgo = now - 24.hours.inWholeMilliseconds
        viewModelScope.launch {
            val count = launchAttemptsRepository.getLaunchAttemptsForPackageAfter(
                from = twentyFourHoursAgo,
                packageName = packageName
            )
            _launchAttemptState.update { count }
        }
    }
}
