package ly.com.tahaben.launcher_data.repository

import ly.com.tahaben.launcher_data.local.db.LaunchAttemptDao
import ly.com.tahaben.launcher_data.mapper.toLaunchAttemptEntity
import ly.com.tahaben.launcher_domain.model.LaunchAttempt
import ly.com.tahaben.launcher_domain.repository.LaunchAttemptsRepository

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 27/3/2025.
 */
class LaunchAttemptsRepoImpl(
    private val dao: LaunchAttemptDao,
//    private val workManager: WorkManager
): LaunchAttemptsRepository {

    override suspend fun insert(launchAttempt: LaunchAttempt) {
        dao.insert(launchAttempt.toLaunchAttemptEntity())
    }

    override suspend fun getLaunchAttemptsForPackageWithinRange(
        from: Long,
        to: Long,
        packageName: String
    ): Int {
        return dao.getLaunchAttemptsForPackageWithinRange(from, to, packageName)
    }

    override suspend fun getLaunchAttemptsForPackageAfter(from: Long, packageName: String): Int {
        return dao.getLaunchAttemptsForPackageAfter(from,packageName)
    }

    override suspend fun clearLaunchAttemptsEarlierThan(timestamp: Long): Int {
        return dao.clearLaunchAttemptsEarlierThan(timestamp)
    }

}