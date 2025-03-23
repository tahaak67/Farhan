plugins {
    id("com.android.library")
    alias(libs.plugins.hiltAndroidGradle)
}
android {
    namespace = "ly.com.tahaben.core"
    defaultConfig {
        vectorDrawables.generatedDensities?.clear()
    }
    androidResources {
        noCompress += "ttf"
        noCompress += ".ttf"
    }
}
apply {
    from("$rootDir/base-module.gradle")
}
apply(plugin = "org.jetbrains.kotlin.android")