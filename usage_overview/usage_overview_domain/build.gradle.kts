plugins {
    id("com.android.library")
}
android {
    namespace = "ly.com.tahaben.usage_overview_domain"
}
apply {
    from("$rootDir/base-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(Coroutines.coroutines)
}