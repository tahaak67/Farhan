plugins {
    id("com.android.library")
    id("dagger.hilt.android.plugin")
}
android {
    namespace = "ly.com.tahaben.infinite_scroll_blocker_presentation"
}
apply {
    from("$rootDir/compose-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.coreUi))
    "implementation"(project(Modules.infiniteScrollBlockerDomain))
}