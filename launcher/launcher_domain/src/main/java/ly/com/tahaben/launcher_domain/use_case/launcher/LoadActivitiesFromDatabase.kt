package ly.com.tahaben.launcher_domain.use_case.launcher

import kotlinx.coroutines.flow.Flow
import ly.com.tahaben.core.model.AppItem
import ly.com.tahaben.launcher_domain.repository.AvailableActivitiesRepository

class LoadActivitiesFromDatabase(
    private val activitiesRepository: AvailableActivitiesRepository
) {
    operator fun invoke(): Flow<List<AppItem>> {
        return activitiesRepository.loadActivitiesFromDb()
    }
}
