plugins {
    id("com.android.library")
    id("dagger.hilt.android.plugin")
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

    "kapt"(Room.roomCompiler)
    "implementation"(Room.roomKtx)
    "implementation"(Room.roomRuntime)
}
