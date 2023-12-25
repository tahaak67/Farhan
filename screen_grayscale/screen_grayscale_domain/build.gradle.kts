plugins {
    id("com.android.library")
}
android {
    namespace = "ly.com.tahaben.screen_grayscale_domain"
}
apply {
    from("$rootDir/base-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
}