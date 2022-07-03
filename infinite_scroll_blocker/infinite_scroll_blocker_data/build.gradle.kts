apply {
    from("$rootDir/base-module.gradle")
}
dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.infiniteScrollBlockerDomain))
    "implementation"("com.google.android.material:material:1.6.1")
}
plugins {
    id("dagger.hilt.android.plugin")
}