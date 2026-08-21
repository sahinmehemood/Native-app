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
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
}

// ── Convention: every Android module shares this baseline ────────────────────
subprojects {
    // Apply Kotlin/Compose defaults uniformly so a 1M-LOC repo stays consistent.
    plugins.withId("org.jetbrains.kotlin.android") {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension> {
            jvmToolchain(17)
        }
    }

    // ktlint: enforce Kotlin style on every module (iOS-app-grade consistency).
    plugins.withId("org.jlleitschuh.gradle.ktlint") {
        extensions.configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
            version.set(libs.versions.ktlint.get())
            android.set(true)
            ignoreFailures.set(false) // hard gate: bad formatting fails CI
        }
    }

    // detekt: static analysis gate (complexity, style, potential bugs).
    plugins.withId("io.gitlab.arturbosch.detekt") {
        extensions.configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
            buildUponDefaultConfig = true
            allRules = false
            config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
            baseline = file("$rootDir/config/detekt/baseline.xml") // suppress pre-existing noise only
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
