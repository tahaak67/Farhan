package ly.com.tahaben.usage_overview_domain.use_case

import timber.log.Timber
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

class FilterDuration {
    operator fun invoke(milliseconds: Long): Long {
        val durationInMilli = milliseconds.milliseconds
        //this sometimes returns an unrealistic large number for hours until this is fixed i will have to manually set it to 0 if its larger than 24 (since we only get usage for 1 day at a time)
        return durationInMilli.toComponents { hours, minutes, _, _ ->
            val hrs = if (abs(hours) > 24L) 0L else hours
            val min = abs(minutes)
            val t = (((hrs * 60) + min) * 60 * 1000).milliseconds.inWholeMilliseconds
            Timber.d("durationinmilli= $t")
            t
        }
    }
}