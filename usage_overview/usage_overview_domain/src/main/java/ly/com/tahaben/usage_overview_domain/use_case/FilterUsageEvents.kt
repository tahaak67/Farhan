package ly.com.tahaben.usage_overview_domain.use_case

import ly.com.tahaben.usage_overview_domain.model.UsageDataItem

class FilterUsageEvents {

    operator fun invoke(
        usageDataItems: List<UsageDataItem>
    ): List<UsageDataItem> {
        return usageDataItems.filter { usageDataItem ->
            usageDataItem.usageType == UsageDataItem.EventType.Start ||
                    usageDataItem.usageType == UsageDataItem.EventType.Stop
        }
    }
}