package ly.com.tahaben.farhan.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ly.com.tahaben.usage_overview_domain.model.UsageDataItem
import ly.com.tahaben.usage_overview_domain.repository.UsageRepository
import java.time.LocalDate

class UsageRepositoryFake : UsageRepository {

    val usageItems = mutableListOf<UsageDataItem>()
    var permissionGranted = true

    override suspend fun getUsageEvents(date: LocalDate): Flow<List<UsageDataItem>> {
        if (checkUsagePermission()) {
            /*usageItems.add(
                UsageDataItem(
                    appName = "Farhan",
                    packageName = "ly.farhan",
                    usageTimestamp = -1659166113695,
                    usageType = UsageDataItem.EventType.Start,
                    appCategory = UsageDataItem.Category.PRODUCTIVITY
                )
            )
            usageItems.add(
                UsageDataItem(
                    appName = "Farhan",
                    packageName = "ly.farhan",
                    usageTimestamp = 1659168892650,
                    usageType = UsageDataItem.EventType.Stop,
                    appCategory = UsageDataItem.Category.PRODUCTIVITY
                )
            )
*/
            return flowOf(usageItems)
        }
        return flowOf(usageItems)
    }

    override fun checkUsagePermission(): Boolean {
        return permissionGranted
    }
}