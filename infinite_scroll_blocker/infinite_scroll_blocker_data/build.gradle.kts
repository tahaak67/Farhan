plugins {
    id("com.android.library")
    id("dagger.hilt.android.plugin")
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
    "implementation"(Google.material)
}
