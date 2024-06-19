pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.5.0")
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Farhan"
include(":app")
include(":core")
include(":core-ui")
include(":infinite_scroll_blocker")
include(":notification_filter")
include(":screen_grayscale")
include(":infinite_scroll_blocker:infinite_scroll_blocker_domain")
include(":infinite_scroll_blocker:infinite_scroll_blocker_presentation")
include(":infinite_scroll_blocker:infinite_scroll_blocker_data")
include(":notification_filter:notification_filter_domain")
include(":notification_filter:notification_filter_presentation")
include(":notification_filter:notification_filter_data")
include(":screen_grayscale:screen_grayscale_domain")
include(":screen_grayscale:screen_grayscale_presentation")
include(":screen_grayscale:screen_grayscale_data")
include(":onboarding")
include(":onboarding:onboarding_presentation")
include(":onboarding:onboarding_domain")
include(":usage_overview")
include(":usage_overview:usage_overview_presentation")
include(":usage_overview:usage_overview_data")
include(":usage_overview:usage_overview_domain")
include(":onboarding:onboarding_data")
include(":launcher")
include(":launcher:launcher_domain")
include(":launcher:launcher_data")
include(":launcher:launcher_presentation")
