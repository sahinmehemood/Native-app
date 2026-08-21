plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.hermes.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hermes.android"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions { jvmTarget = "17" }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    // Core + feature modules
    implementation(project(":core:common"))
    implementation(project(":core:design"))
    implementation(project(":core:gateway"))
    implementation(project(":core:data"))
    implementation(project(":core:navigation"))
    implementation(project(":core:ui"))
    implementation(project(":feature:home"))
    implementation(project(":feature:chat"))
    implementation(project(":feature:activity"))
    implementation(project(":feature:sessions"))
    implementation(project(":feature:nous"))
    implementation(project(":feature:automations"))
    implementation(project(":feature:settings"))

    // Compose + AndroidX (already via BOM in modules; app adds tooling/test)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Gateway client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.json)

    // DI + storage
    implementation(libs.koin.android)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.security.crypto)
    implementation(libs.kotlinx.datetime)

    // Tests
    testImplementation(libs.test.junit)
    testImplementation(libs.test.junit.ktx)
    testImplementation(libs.test.mockk)
    testImplementation(libs.test.turbine)
    testImplementation(libs.kotlinx.coroutines.android)
    androidTestImplementation(libs.test.junit.ktx)
    androidTestImplementation(libs.test.espresso)
    androidTestImplementation(libs.test.compose.ui)
    debugImplementation(libs.test.compose.ui.manifest)
}
