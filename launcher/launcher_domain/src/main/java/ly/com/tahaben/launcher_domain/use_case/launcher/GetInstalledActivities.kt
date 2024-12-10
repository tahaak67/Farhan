package ly.com.tahaben.launcher_domain.use_case.launcher

import ly.com.tahaben.core.model.AppItem
import ly.com.tahaben.launcher_domain.repository.AvailableActivitiesRepository

class GetInstalledActivities(
    private val activitiesRepository: AvailableActivitiesRepository
) {

    suspend operator fun invoke(): List<AppItem> {
        return activitiesRepository.getActivities()
    }
}