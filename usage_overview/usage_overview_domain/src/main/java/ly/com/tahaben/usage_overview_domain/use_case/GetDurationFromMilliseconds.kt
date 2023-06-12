package ly.com.tahaben.usage_overview_domain.use_case

import ly.com.tahaben.core.R
import ly.com.tahaben.core.util.UiText
import kotlin.time.Duration.Companion.milliseconds

class GetDurationFromMilliseconds {

    operator fun invoke(milliseconds: Long): UiText {
        val durationInMilli = milliseconds.milliseconds

        return durationInMilli.toComponents { hours, minutes, _, _ ->

            when (hours) {
                1L -> UiText.StringResource(R.string.one_hour)
                2L -> UiText.StringResource(R.string.two_hours)
                else -> {
                    if (hours == 0L) {
                        UiText.MixedString(minutes, R.string.minutes)
                    } else if (minutes == 0) {
                        UiText.MixedString(hours.toInt(), R.string.hours)
                    } else {
                        UiText.TimeFormatString(
                            hours.toInt(),
                            R.string.hours,
                            minutes,
                            R.string.minutes
                        )
                    }
                }
            }
        }
    }

}