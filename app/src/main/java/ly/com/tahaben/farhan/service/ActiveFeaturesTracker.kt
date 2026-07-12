package ly.com.tahaben.farhan.service

import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import ly.com.tahaben.core.service.ActiveFeature
import ly.com.tahaben.core.service.RunningServicesNotifier
import ly.com.tahaben.infinite_scroll_blocker_domain.use_cases.InfiniteScrollUseCases
import ly.com.tahaben.launcher_domain.preferences.Preference
import ly.com.tahaben.notification_filter_domain.use_cases.NotificationFilterUseCases
import ly.com.tahaben.screen_grayscale_domain.use_cases.GrayscaleUseCases
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import ly.com.tahaben.infinite_scroll_blocker_domain.preferences.Preferences as InfiniteScrollPreferences
import ly.com.tahaben.notification_filter_domain.preferences.Preferences as NotificationFilterPreferences
import ly.com.tahaben.screen_grayscale_domain.preferences.Preferences as GrayscalePreferences

/**
 * Watches every feature toggle that the ongoing "Farhan is active" notification reports on and
 * pushes the current set of enabled features to [RunningServicesNotifier], so the notification
 * only ever lists features that are really on (each toggle check also implies the app main
 * switch is on).
 *
 * Recomputes on any change to the toggles' backing stores: the feature switches kept in
 * SharedPreferences and the DataStore holding the main switch and the delayed launch/unlock
 * switches.
 */
@Singleton
class ActiveFeaturesTracker @Inject constructor(
    private val sharedPref: SharedPreferences,
    private val dataStore: DataStore<Preferences>,
    private val notificationFilterUseCases: NotificationFilterUseCases,
    private val infiniteScrollUseCases: InfiniteScrollUseCases,
    private val grayscaleUseCases: GrayscaleUseCases,
    private val launcherPreferences: Preference,
    private val runningServicesNotifier: RunningServicesNotifier
) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun start() {
        scope.launch {
            merge(sharedPrefToggleChanges(), dataStore.data.map { })
                .conflate()
                .collect {
                    val features = enabledFeatures()
                    Timber.d("enabled features: $features")
                    runningServicesNotifier.setEnabledFeatures(features)
                }
        }
    }

    private fun sharedPrefToggleChanges(): Flow<Unit> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            // key == null signals the whole store was cleared
            if (key == null || key in TOGGLE_KEYS) {
                trySend(Unit)
            }
        }
        sharedPref.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { sharedPref.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    private suspend fun enabledFeatures(): Set<ActiveFeature> = buildSet {
        if (notificationFilterUseCases.checkIfNotificationServiceIsEnabled()) {
            add(ActiveFeature.NOTIFICATION_FILTER)
        }
        if (grayscaleUseCases.isGrayscaleEnabled()) {
            add(ActiveFeature.GRAYSCALE)
        }
        if (infiniteScrollUseCases.isServiceEnabled()) {
            add(ActiveFeature.INFINITE_SCROLL_BLOCKER)
        }
        if (launcherPreferences.isDelayedLaunchEnabled().first()) {
            add(ActiveFeature.DELAYED_LAUNCH)
        }
        if (launcherPreferences.isDelayedUnlockEnabled().first()) {
            add(ActiveFeature.DELAYED_UNLOCK)
        }
    }

    companion object {
        private val TOGGLE_KEYS = setOf(
            NotificationFilterPreferences.KEY_NOTIFICATION_SERVICE_STATS,
            InfiniteScrollPreferences.KEY_INFINITE_SCROLL_SERVICE_STATS,
            GrayscalePreferences.KEY_GRAYSCALE_SERVICE_STATS
        )
    }
}
