apply {
    from("$rootDir/base-module.gradle")
}
plugins {
    id("dagger.hilt.android.plugin")
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.notificationFilterDomain))

    "kapt"(Room.roomCompiler)
    "implementation"(Room.roomKtx)
    "implementation"(Room.roomRuntime)
}