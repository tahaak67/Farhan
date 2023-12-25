plugins {
    id("com.android.library")
}
android {
    namespace = "ly.com.tahaben.onboarding_domain"
}

apply {
    from("$rootDir/base-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
}