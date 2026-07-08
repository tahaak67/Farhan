package ly.com.tahaben.launcher_presentation.launcher

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ly.com.tahaben.core.model.AppItem
import ly.com.tahaben.core.util.SearchEvent
import ly.com.tahaben.launcher_domain.model.LaunchAttempt
import ly.com.tahaben.launcher_domain.model.TimeLimit
import ly.com.tahaben.launcher_domain.preferences.Preference
import ly.com.tahaben.launcher_domain.repository.LaunchAttemptsRepository
import ly.com.tahaben.launcher_domain.use_case.launcher.LauncherUseCases
import ly.com.tahaben.launcher_domain.use_case.time_limit.TimeLimitUseCases
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val launcherUseCases: LauncherUseCases,
    private val timeLimitUseCases: TimeLimitUseCases,
    private val preference: Preference,
    private val launchAttemptsRepository: LaunchAttemptsRepository
) : ViewModel() {

    var state by mutableStateOf(LauncherState())
        private set


    private var getInstalledActivitiesJob: Job? = null
    private var latestActivities = listOf<AppItem>()
    private var selectedDelayMessage = ""
    private var delayMessages = emptySet<String>()

    init {
        refreshApps()
        viewModelScope.launch {
            launch {
                preference.getDelayedLaunchDuration().collectLatest { duration ->
                    state = state.copy(delayDurationSeconds = duration)
                }
            }
            launch {
                preference.getDelayedLaunchMessage().collectLatest { message ->
                    selectedDelayMessage = message
                }
            }
            launch {
                preference.getDelayedLaunchMessages().collectLatest { messages ->
                    delayMessages = messages
                }
            }
        }
    }

    fun refreshApps() {
        viewModelScope.launch {
            latestActivities = launcherUseCases.getInstalledActivities()
            Timber.d("latest: ${latestActivities.size}")
            getInstalledActivitiesJob?.cancel()
            getInstalledActivitiesJob = launcherUseCases.loadActivitiesFromDB()
                .onEach { apps ->
                    state = state.copy(
                        appsList = apps,
                        searchResults = apps,
                        isLoading = false
                    )
                    if (state.query.isNotBlank()) {
                        executeSearch()
                    }
                    Timber.d("latestdb: ${apps.size}")

                    val notAvailableApps = apps.filterNot {
                        latestActivities.contains(it)
                    }
                    Timber.d("notavailable apps $notAvailableApps")
                    notAvailableApps.forEach { item ->
                        launcherUseCases.removeAppFromDB(item)
                        Timber.d("removed $item")
                    }
                }
                .launchIn(viewModelScope)
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
            // not literally hidding search in this one, just clearing text and hiding keyboard
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

    fun onAppClick(app: AppItem) {
        if (timeLimitUseCases.isPackageInTimeLimitWhiteList(app.packageName)) {
            showTimeLimitDialogFor(app)
//            delayLaunch(app)
        } else {
            launchActivityForApp(app)
        }
    }

    fun launchActivityForApp(app: AppItem) {
        launcherUseCases.launchMainActivityForApp(app)
        dismissTimeLimitDialog()
    }

    fun showTimeLimitDialogFor(app: AppItem) {
        state = state.copy(
            isTimeLimitDialogVisible = true,
            timeLimitedApp = app
        )
    }

    fun setTimeLimitAndLunchApp(app: AppItem, timeLimitInMinutes: Int) {
        viewModelScope.launch {
            timeLimitUseCases.addTimeLimitToDb(
                TimeLimit(
                    app.packageName,
                    timeLimitInMinutes.minutes.inWholeMilliseconds,
                    System.currentTimeMillis()
                )
            )
        }
        launchActivityForApp(app)
        dismissTimeLimitDialog()
    }

    fun dismissTimeLimitDialog() {
        state = state.copy(
            isTimeLimitDialogVisible = false
        )
    }

    fun launchAppInfo(app: AppItem) {
        launcherUseCases.launchAppInfo(app)
    }

    fun launchDefaultDialerApp() {
        launcherUseCases.launchDefaultDialer()
    }

    fun launchDefaultCameraApp() {
        launcherUseCases.launchDefaultCameraApp()
    }

    fun launchDefaultAlarmApp() {
        launcherUseCases.launchDefaultAlarmApp()
    }

    fun delayLaunch(app: AppItem) {
        viewModelScope.launch {
            launchAttemptsRepository.insert(
                LaunchAttempt(
                    id = 0,
                    packageName = app.packageName,
                    timestamp = System.currentTimeMillis()
                )
            )
            val attemptCount = launchAttemptsRepository.getLaunchAttemptsForPackageAfter(
                from = System.currentTimeMillis() - 24.hours.inWholeMilliseconds,
                packageName = app.packageName
            )
            state = state.copy(
                isDelayRunning = true,
                timeLimitedApp = app,
                delayMessage = selectedDelayMessage.ifEmpty { delayMessages.randomOrNull() ?: "" },
                launchAttemptCount = attemptCount
            )
        }
    }

    fun disableOverlay() {
        state = state.copy(
            isDelayRunning = false
        )
    }
}