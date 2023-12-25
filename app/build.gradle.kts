plugins {
    id("com.android.application")
    kotlin("android")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
}

android {
    namespace = "ly.com.tahaben.farhan"
    compileSdk = ProjectConfig.compileSdk

    defaultConfig {
        applicationId = ProjectConfig.appId
        minSdk = ProjectConfig.minSdk
        targetSdk = ProjectConfig.targetSdk
        versionCode = ProjectConfig.versionCode
        versionName = ProjectConfig.versionName

        testInstrumentationRunner = "ly.com.tahaben.farhan.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    composeOptions {
        kotlinCompilerExtensionVersion = Compose.composeCompilerVersion
    }
    packagingOptions {
        resources.excludes.add("META-INF/AL2.0")
        resources.excludes.add("META-INF/LGPL2.1")
        resources.excludes.add("**/attach_hotspot_windows.dll")
        resources.excludes.add("META-INF/licenses/ASM")
        resources.excludes.add("META-INF/LICENSE.md")
        resources.excludes.add("META-INF/LICENSE-notice.md")
    }
}

dependencies {
    implementation(Compose.compiler)
    implementation(Compose.ui)
    implementation(Compose.uiToolingPreview)
    implementation(Compose.hiltNavigationCompose)
    implementation(Compose.material3)
    implementation(Compose.runtime)
    implementation(Compose.navigation)
    implementation(Compose.viewModelCompose)
    implementation(Compose.lifecycleUtilityCompose)
    implementation(Compose.activityCompose)

    implementation(DaggerHilt.hiltAndroid)
    kapt(DaggerHilt.hiltCompiler)

    implementation(Timber.timber)
    debugImplementation(LeakCanary.leakCanary)

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

    implementation(AndroidX.coreKtx)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.workManger)

    implementation(Coil.coilCompose)

    implementation(Google.material)
    implementation(Google.accompanistUiController)

    kapt(Room.roomCompiler)
    implementation(Room.roomKtx)
    implementation(Room.roomRuntime)

    testImplementation(Testing.junit4)
    testImplementation(Testing.junitAndroidExt)
    testImplementation(Testing.truth)
    testImplementation(Testing.coroutines)
    testImplementation(Testing.turbine)
    testImplementation(Testing.composeUiTest)
    testImplementation(Testing.mockk)


    androidTestImplementation(Testing.junit4)
    androidTestImplementation(Testing.junitAndroidExt)
    androidTestImplementation(Testing.truth)
    androidTestImplementation(Testing.coroutines)
    androidTestImplementation(Testing.turbine)
    androidTestImplementation(Testing.composeUiTest)
    androidTestImplementation(Testing.mockkAndroid)

    androidTestImplementation(Testing.hiltTesting)
    kaptAndroidTest(DaggerHilt.hiltCompiler)
    androidTestImplementation(Testing.testRunner)
    // Dependency required for API desugaring.
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs_nio:2.0.4")
}