package ly.com.tahaben.launcher_domain.use_case.launcher

import ly.com.tahaben.core.model.AppItem
import ly.com.tahaben.launcher_domain.repository.AvailableActivitiesRepository

class RemoveAppFromDB(private val availableActivitiesRepo: AvailableActivitiesRepository) {
    suspend operator fun invoke(app: AppItem) {
        availableActivitiesRepo.deleteActivity(app)
    }

}
