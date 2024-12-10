package ly.com.tahaben.core.navigation

import ly.com.tahaben.core.navigation.Args.END_DATE
import ly.com.tahaben.core.navigation.Args.START_DATE

object Routes {

    const val WELCOME = "welcome"
    const val SELECT_THEME = "select_theme"
    const val MAIN = "main"
    const val USAGE = "usage"
    const val USAGE_FOR_PERIOD = "$USAGE/{$START_DATE}/{$END_DATE}"
    const val USAGE_SETTINGS = "usage_settings"
    const val NOTIFICATION_FILTER = "notification_filter"
    const val NOTIFICATION_FILTER_SETTINGS = "notification_filter_settings"
    const val NOTIFICATION_FILTER_EXCEPTIONS = "notification_filter_exceptions"
    const val INFINITE_SCROLLING = "infinite_scrolling"
    const val INFINITE_SCROLLING_EXCEPTIONS = "infinite_scrolling_exceptions"
    const val SCREEN_GRAY_SCALE = "screen_grayscale"
    const val SCREEN_GRAY_SCALE_WHITE_LIST = "screen_grayscale_exceptions"
    const val ABOUT_APP = "about_app"
    const val LAUNCHER = "launcher"
    const val LAUNCHER_SETTINGS = "launcher_settings"
    const val TimeLimiter_SETTINGS = "time_limiter_settings"
    const val TimeLimiter_WHITELIST_SETTINGS = "time_limiter_whitelist_settings"
    const val MINDFUL_LAUNCH_SETTINGS = "mindful_launch_settings"
    const val MINDFUL_LAUNCH_WHITELIST = "mindful_launch_whitelist"

}