package ly.com.tahaben.launcher_domain.use_case

import ly.com.tahaben.launcher_domain.repository.AvailableActivitiesRepository

class LaunchDefaultDialer(private val activitiesRepository: AvailableActivitiesRepository) {
    operator fun invoke() {
        activitiesRepository.launchDefaultDialerApp()
    }
}
