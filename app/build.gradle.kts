plugins {
    // AGP 9 has built-in Kotlin support; no kotlin-android plugin needed.
    id("com.android.application") version "9.1.1"
    // Compose compiler plugin must match AGP 9.1.1's built-in Kotlin version.
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.10"
}

android {
    namespace = "me.fujunhao.engstudy"
    compileSdk = 37

    defaultConfig {
        applicationId = "me.fujunhao.engstudy"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2026.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.core:core-ktx:1.17.0")

    // Liquid glass effect primitives (iOS-style backdrop blur / lens / highlight / shadow)
    implementation("io.github.kyant0:backdrop:2.0.1")
}
