plugins {
    id("com.android.application")
    kotlin("android")
    alias(libs.plugins.hiltAndroidGradle)
    alias(libs.plugins.compose.compiler)
    id("com.google.devtools.ksp")
}

android {
    namespace = "ly.com.tahaben.farhan"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "ly.com.tahaben.farhan"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = libs.versions.app.versionCode.get().toInt()
        versionName = libs.versions.app.versionName.get()

        testInstrumentationRunner = "ly.com.tahaben.farhan.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        vectorDrawables.generatedDensities?.clear()
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    androidResources {
        noCompress += "ttf"
        noCompress += ".ttf"
    }
    packaging {
        resources.excludes.add("META-INF/AL2.0")
        resources.excludes.add("META-INF/LGPL2.1")
        resources.excludes.add("**/attach_hotspot_windows.dll")
        resources.excludes.add("META-INF/licenses/ASM")
        resources.excludes.add("META-INF/LICENSE.md")
        resources.excludes.add("META-INF/LICENSE-notice.md")
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.hilt.navigation)
    implementation(libs.compose.runtime)
    implementation(libs.compose.navigation)
    implementation(libs.compose.viewModel)
    implementation(libs.compose.lifecycle.runtime)
    implementation(libs.compose.activity)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.extended)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)

    implementation(libs.timber)
//    debugImplementation(libs.leakCanary)

    implementation(project(Modules.core))
    implementation(project(Modules.coreUi))
    implementation(project(Modules.infiniteScrollBlockerData))
    implementation(project(Modules.infiniteScrollBlockerDomain))
    implementation(project(Modules.infiniteScrollBlockerPresentation))
    implementation(project(Modules.notificationFilterData))
    implementation(project(Modules.notificationFilterDomain))
    implementation(project(Modules.notificationFilterPresentation))
    implementation(project(Modules.grayScaleData))
    implementation(project(Modules.grayScaleDomain))
    implementation(project(Modules.grayScalePresentation))
    implementation(project(Modules.onboardingDomain))
    implementation(project(Modules.onboardingData))
    implementation(project(Modules.onboardingPresentation))
    implementation(project(Modules.usageOverviewDomain))
    implementation(project(Modules.usageOverviewData))
    implementation(project(Modules.usageOverviewPresentation))
    implementation(project(Modules.launcherData))
    implementation(project(Modules.launcherDomain))
    implementation(project(Modules.launcherPresentation))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.work.manager)

    implementation(libs.coil.compose)

    implementation(libs.google.material)
    implementation(libs.accompanist.ui.controller)

    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)

    implementation(libs.glance.appWidget)
    implementation(libs.glance.material3)

    testImplementation(libs.junit4)
    testImplementation(libs.test.runner)
    testImplementation(libs.truth)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.compose.ui.test)


    androidTestImplementation(libs.junit4)
    androidTestImplementation(libs.junit.android.ext)
    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.coroutines.test)
    androidTestImplementation(libs.turbine)
    androidTestImplementation(libs.compose.ui.test)


    androidTestImplementation(libs.hilt.testing)
    kspAndroidTest(libs.dagger.hilt.compiler)
    androidTestImplementation(libs.test.runner)

    // Dependency required for API desugaring.
    coreLibraryDesugaring(libs.desugar.jdk.libs.nio)
}