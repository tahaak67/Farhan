plugins {
    id("com.android.library")
}
android {
    namespace = "ly.com.tahaben.infinite_scroll_blocker_domain"
}
apply {
    from("$rootDir/base-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
}