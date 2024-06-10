plugins {
    id("com.android.library")
    alias(libs.plugins.hiltAndroidGradle)
}
android {
    namespace = "ly.com.tahaben.notification_filter_data"
}
apply {
    from("$rootDir/base-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.notificationFilterDomain))

    "kapt"(libs.room.compiler)
    "implementation"(libs.room.ktx)
    "implementation"(libs.room.runtime)
}
