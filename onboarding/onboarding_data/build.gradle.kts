plugins {
    id("com.android.library")
}
android {
    namespace = "ly.com.tahaben.onboarding_data"
}
apply {
    from("$rootDir/base-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.onboardingDomain))
}