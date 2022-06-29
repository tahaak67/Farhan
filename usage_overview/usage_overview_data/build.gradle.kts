apply {
    from("$rootDir/base-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.usageOverviewDomain))
    "implementation"(Coroutines.coroutines)
    "implementation"("androidx.appcompat:appcompat:1.4.2")
}