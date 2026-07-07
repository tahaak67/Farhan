package ly.com.tahaben.screen_grayscale_domain.use_cases

import com.google.common.truth.Truth.assertThat
import ly.com.tahaben.core.data.repository.InstalledAppsRepository
import ly.com.tahaben.core.model.AppItem
import ly.com.tahaben.screen_grayscale_domain.model.GrayscaleAppState
import ly.com.tahaben.screen_grayscale_domain.preferences.Preferences
import org.junit.Before
import org.junit.Test

private const val SYSTEM_APP = "com.android.systemui"
private const val USER_APP = "com.example.userapp"

class AppGrayscaleStateTest {

    private lateinit var preferences: FakeGrayscalePreferences
    private lateinit var getAppGrayscaleState: GetAppGrayscaleState
    private lateinit var setAppGrayscaleState: SetAppGrayscaleState

    @Before
    fun setUp() {
        preferences = FakeGrayscalePreferences()
        val installedAppsRepo = object : InstalledAppsRepository {
            override suspend fun getInstalledApps(): List<AppItem> = emptyList()
            override fun isSystemApp(packageName: String): Boolean = packageName == SYSTEM_APP
        }
        getAppGrayscaleState = GetAppGrayscaleState(preferences, installedAppsRepo)
        setAppGrayscaleState = SetAppGrayscaleState(preferences)
    }

    @Test
    fun `user app without explicit choice defaults to color`() {
        assertThat(getAppGrayscaleState(USER_APP)).isEqualTo(GrayscaleAppState.COLOR)
    }

    @Test
    fun `system app without explicit choice defaults to leave as is`() {
        assertThat(getAppGrayscaleState(SYSTEM_APP)).isEqualTo(GrayscaleAppState.LEAVE_AS_IS)
    }

    @Test
    fun `whitelisted app resolves to grayscale`() {
        setAppGrayscaleState(USER_APP, GrayscaleAppState.GRAYSCALE)

        assertThat(getAppGrayscaleState(USER_APP)).isEqualTo(GrayscaleAppState.GRAYSCALE)
    }

    @Test
    fun `whitelisted system app resolves to grayscale`() {
        setAppGrayscaleState(SYSTEM_APP, GrayscaleAppState.GRAYSCALE)

        assertThat(getAppGrayscaleState(SYSTEM_APP)).isEqualTo(GrayscaleAppState.GRAYSCALE)
    }

    @Test
    fun `system app explicitly set to color resolves to color`() {
        setAppGrayscaleState(SYSTEM_APP, GrayscaleAppState.COLOR)

        assertThat(getAppGrayscaleState(SYSTEM_APP)).isEqualTo(GrayscaleAppState.COLOR)
    }

    @Test
    fun `user app explicitly set to leave as is resolves to leave as is`() {
        setAppGrayscaleState(USER_APP, GrayscaleAppState.LEAVE_AS_IS)

        assertThat(getAppGrayscaleState(USER_APP)).isEqualTo(GrayscaleAppState.LEAVE_AS_IS)
    }

    @Test
    fun `changing state overrides the previous explicit choice`() {
        setAppGrayscaleState(USER_APP, GrayscaleAppState.GRAYSCALE)
        setAppGrayscaleState(USER_APP, GrayscaleAppState.LEAVE_AS_IS)

        assertThat(getAppGrayscaleState(USER_APP)).isEqualTo(GrayscaleAppState.LEAVE_AS_IS)
        assertThat(preferences.isPackageInInfiniteScrollExceptions(USER_APP)).isFalse()

        setAppGrayscaleState(USER_APP, GrayscaleAppState.COLOR)

        assertThat(getAppGrayscaleState(USER_APP)).isEqualTo(GrayscaleAppState.COLOR)
        assertThat(preferences.isPackageInGrayscaleAgnosticList(USER_APP)).isFalse()
    }

    @Test
    fun `whitelist saved before this feature is still honored`() {
        // simulates a set persisted by an older version of the app
        preferences.savePackageToInfiniteScrollExceptions(USER_APP)

        assertThat(getAppGrayscaleState(USER_APP)).isEqualTo(GrayscaleAppState.GRAYSCALE)
    }
}

private class FakeGrayscalePreferences : Preferences {

    private val whiteList = mutableSetOf<String>()
    private val agnosticList = mutableSetOf<String>()
    private val coloredList = mutableSetOf<String>()
    private var shouldShowOnBoarding = true
    private var serviceEnabled = false

    override fun loadShouldShowOnBoarding(): Boolean = shouldShowOnBoarding

    override fun saveShouldShowOnBoarding(shouldShow: Boolean) {
        shouldShowOnBoarding = shouldShow
    }

    override suspend fun isGrayscaleEnabled(): Boolean = serviceEnabled

    override fun setServiceState(isEnabled: Boolean) {
        serviceEnabled = isEnabled
    }

    override fun savePackageToInfiniteScrollExceptions(packageName: String) {
        whiteList.add(packageName)
    }

    override fun removePackageFromInInfiniteScrollExceptions(packageName: String) {
        whiteList.remove(packageName)
    }

    override fun isPackageInInfiniteScrollExceptions(packageName: String): Boolean =
        packageName in whiteList

    override fun getInInfiniteScrollExceptionsList(): Set<String> = whiteList.toSet()

    override fun savePackageToGrayscaleAgnosticList(packageName: String) {
        agnosticList.add(packageName)
    }

    override fun removePackageFromGrayscaleAgnosticList(packageName: String) {
        agnosticList.remove(packageName)
    }

    override fun isPackageInGrayscaleAgnosticList(packageName: String): Boolean =
        packageName in agnosticList

    override fun savePackageToGrayscaleColoredList(packageName: String) {
        coloredList.add(packageName)
    }

    override fun removePackageFromGrayscaleColoredList(packageName: String) {
        coloredList.remove(packageName)
    }

    override fun isPackageInGrayscaleColoredList(packageName: String): Boolean =
        packageName in coloredList
}
