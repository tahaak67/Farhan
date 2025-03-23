package ly.com.tahaben.launcher_domain.use_case.launcher

import ly.com.tahaben.launcher_domain.repository.AvailableActivitiesRepository

class LaunchDefaultAlarmApp(private val activitiesRepository: AvailableActivitiesRepository) {
    operator fun invoke() {
        activitiesRepository.launchDefaultAlarmApp()
    }
}
