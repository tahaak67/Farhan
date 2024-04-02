buildscript {

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.dagger.hilt.plugin)
    }
}
plugins {
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.androidApplication) apply false
}