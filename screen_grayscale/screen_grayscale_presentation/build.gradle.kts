plugins {
    id("com.android.library")
}
android {
    namespace = "ly.com.tahaben.screen_grayscale_presentation"
}
apply {
    from("$rootDir/compose-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.coreUi))
    "implementation"(project(Modules.grayScaleDomain))
}