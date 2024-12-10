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
}