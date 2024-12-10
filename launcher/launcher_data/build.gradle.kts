plugins {
    id("com.android.library")
    alias(libs.plugins.hiltAndroidGradle)
}
android {
    namespace = "ly.com.tahaben.launcher_data"
}
apply {
    // we need compose dependencies to show popups form the background service
    from("$rootDir/compose-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.launcherDomain))
    implementation(project(":core-ui"))
    implementation(libs.androidx.ui.android)
    "kapt"(libs.room.compiler)
    "implementation"(libs.room.ktx)
    "implementation"(libs.room.runtime)
    "implementation"(libs.coroutines.core)
}