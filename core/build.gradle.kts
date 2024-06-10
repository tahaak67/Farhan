plugins {
    id("com.android.library")
    alias(libs.plugins.hiltAndroidGradle)
}
android {
    namespace = "ly.com.tahaben.core"
}
apply {
    from("$rootDir/base-module.gradle")
}
apply(plugin = "org.jetbrains.kotlin.android")