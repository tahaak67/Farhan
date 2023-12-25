plugins {
    id("com.android.library")
}
android {
    namespace = "ly.com.tahaben.onboarding_presentaion"
}
apply {
    from("$rootDir/compose-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.coreUi))
    "implementation"(project(Modules.onboardingDomain))
    "implementation"(Google.accompanistPager)
    "implementation"(ShowcaseLayoutCompose.showcaseLayout)
}