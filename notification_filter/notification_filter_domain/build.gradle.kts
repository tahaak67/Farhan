plugins {
    id("com.android.library")
}
android {
    namespace = "ly.com.tahaben.notification_filter_domain"
}
apply {
    from("$rootDir/base-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(libs.coroutines.core)
}