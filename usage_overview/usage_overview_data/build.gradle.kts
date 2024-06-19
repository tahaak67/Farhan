plugins {
    id("com.android.library")
}
android {
    namespace = "ly.com.tahaben.usage_overview_data"
}
apply {
    from("$rootDir/base-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.usageOverviewDomain))
    "implementation"(libs.coroutines.core)
    "implementation"(libs.androidx.appcompat)
    "kapt"(libs.room.compiler)
    "implementation"(libs.room.ktx)
    "implementation"(libs.room.ktx)
    "implementation"(libs.androidx.work.manager)
}