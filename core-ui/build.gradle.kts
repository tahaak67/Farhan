plugins {
    id("com.android.library")
}
android {
    namespace = "ly.com.tahaben.core_ui"
}
apply {
    from("$rootDir/compose-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(Coil.coilCompose)
    "implementation"(Coil.coilGif)
}