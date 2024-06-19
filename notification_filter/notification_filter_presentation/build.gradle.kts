plugins {
    id("com.android.library")
}
android {
    namespace = "ly.com.tahaben.notification_filter_presentation"
}
apply {
    from("$rootDir/compose-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.coreUi))
    "implementation"(project(Modules.notificationFilterDomain))
    "implementation"(libs.showcase.layout.compose)
}