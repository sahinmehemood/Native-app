// Root project — shared configuration only.
// Per-module behavior lives in each module's build.gradle.kts via the
// `com.android.library` / `hermes-android-module` conventions defined below.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
}

// ── Convention: every Android module shares this baseline ────────────────────
subprojects {
    // Apply Kotlin/Compose defaults uniformly so a 1M-LOC repo stays consistent.
    plugins.withId("org.jetbrains.kotlin.android") {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension> {
            jvmToolchain(17)
        }
    }
    plugins.withId("com.android.library") {
        extensions.configure<com.android.build.gradle.LibraryExtension> {
            compileSdk = 34
            defaultConfig { minSdk = 26 }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
            // Fail fast on missing/untranslated strings and strict resource IDs.
            lint {
                abortOnError = true
                warningsAsErrors = false
                checkDependencies = true
            }
        }
    }
}
