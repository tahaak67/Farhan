apply {
    from("$rootDir/compose-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(Coil.coilCompose)
    "implementation"(Coil.coilGif)
}