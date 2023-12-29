plugins {
    id("com.android.library")
    id("dagger.hilt.android.plugin")
}
android {
    namespace = "ly.com.tahaben.core"
}
apply {
    from("$rootDir/base-module.gradle")
}
apply(plugin = "org.jetbrains.kotlin.android")