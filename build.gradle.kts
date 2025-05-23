buildscript {

    repositories {
        google()
        mavenCentral()
    }
}
plugins {
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.hiltAndroidGradle) apply false
    alias(libs.plugins.compose.compiler) apply false
    id("com.google.devtools.ksp") version "2.1.10-1.0.31" apply false
    alias(libs.plugins.room) apply false
}