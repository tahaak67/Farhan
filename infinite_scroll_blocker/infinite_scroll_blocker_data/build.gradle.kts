apply {
    from("$rootDir/base-module.gradle")
}
dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.infiniteScrollBlockerDomain))
    "implementation"(Google.material)
}
plugins {
    id("dagger.hilt.android.plugin")
}