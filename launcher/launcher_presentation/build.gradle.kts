plugins {
    id("com.android.library")
    alias(libs.plugins.hiltAndroidGradle)
}
android {
    namespace = "ly.com.tahaben.launcher_presentation"
}
apply {
    from("$rootDir/compose-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.coreUi))
    "implementation"(project(Modules.launcherDomain))
}
