package ly.com.tahaben.launcher_domain.use_case

import ly.com.tahaben.core.model.AppItem
import ly.com.tahaben.launcher_domain.repository.AvailableActivitiesRepository

class LaunchMainActivityForApp(private val activitiesRepository: AvailableActivitiesRepository) {
    operator fun invoke(app: AppItem) {
        activitiesRepository.launchMainActivityFor(app)
    }
}
