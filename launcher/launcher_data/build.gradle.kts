plugins {
    id("com.android.library")
}
android {
    namespace = "ly.com.tahaben.launcher_data"
}
apply {
    from("$rootDir/base-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.launcherDomain))
}