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
    "implementation"(Coroutines.coroutines)
    "implementation"(AndroidX.appCompat)
    "kapt"(Room.roomCompiler)
    "implementation"(Room.roomKtx)
    "implementation"(Room.roomRuntime)
    "implementation"(AndroidX.workManger)
}