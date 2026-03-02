plugins {
    id("com.android.library")
    alias(libs.plugins.hiltAndroidGradle)
}
android {
    namespace = "ly.com.tahaben.launcher_data"
}
apply {
    from("$rootDir/base-module.gradle")
}
dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.infiniteScrollBlockerDomain))
    implementation(libs.androidx.datastore)
}
