import org.gradle.api.tasks.Sync

plugins {
    // AGP 9 has built-in Kotlin support; no kotlin-android plugin needed.
    id("com.android.application") version "9.1.1"
    // Compose compiler plugin must match AGP 9.1.1's built-in Kotlin version.
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.4.10"
    id("com.google.devtools.ksp") version "2.3.12"
    id("com.google.dagger.hilt.android") version "2.60.1"
}

// The API origin is deployment-specific and never lives in the repository.
// Pass -PapiBaseUrl=... or API_BASE_URL=...; the placeholder fails loudly.
val apiBaseUrl: String = (project.findProperty("apiBaseUrl") as String?)
    ?: providers.environmentVariable("API_BASE_URL").orNull
    ?: "https://daily-english.invalid/"

android {
    namespace = "me.fujunhao.engstudy"
    compileSdk = 37

    defaultConfig {
        applicationId = "me.fujunhao.engstudy"
        minSdk = 26
        targetSdk = 37
        versionCode = 3
        versionName = "0.2.0"
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
    }

    signingConfigs {
        // Stable signing: CI decodes the keystore from repository secrets and
        // exports KEYSTORE_PATH / KEYSTORE_PASSWORD / KEY_ALIAS / KEY_PASSWORD.
        create("stable") {
            val keystorePath = System.getenv("KEYSTORE_PATH")
            if (!keystorePath.isNullOrBlank()) {
                storeFile = file(keystorePath)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            val keystorePath = System.getenv("KEYSTORE_PATH")
            if (!keystorePath.isNullOrBlank()) {
                signingConfig = signingConfigs.getByName("stable")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

// The corpus lives once, in ./content at the repository root. Gradle copies it
// into assets before every build so the APK never carries a hand-maintained
// second copy.
val contentSource = rootProject.projectDir.resolve("content")
val syncContent = tasks.register<Sync>("syncContent") {
    val source: File = contentSource
    from(source) {
        include("words.json", "sentences.json", "papers/**")
    }
    into(layout.projectDirectory.dir("src/main/assets/content"))
    doFirst {
        check(source.isDirectory) {
            "Missing content directory: ${source.canonicalPath}."
        }
    }
}
tasks.named("preBuild") { dependsOn(syncContent) }

dependencies {
    implementation(platform("androidx.compose:compose-bom:2026.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")

    // Liquid glass effect primitives (iOS-style backdrop blur / lens / highlight / shadow)
    implementation("io.github.kyant0:backdrop:2.0.1")

    // Navigation 3 keeps the back stack while the shell stays a single activity.
    implementation("androidx.navigation3:navigation3-runtime:1.0.1")
    implementation("androidx.navigation3:navigation3-ui:1.0.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-navigation3:2.10.0")

    // Domain / data stack shared with the Liquid Glass baseline.
    implementation("com.google.dagger:hilt-android:2.60.1")
    ksp("com.google.dagger:hilt-android-compiler:2.60.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")
    implementation("androidx.hilt:hilt-work:1.3.0")
    ksp("androidx.hilt:hilt-compiler:1.3.0")

    implementation("androidx.room:room-runtime:2.8.5")
    implementation("androidx.room:room-ktx:2.8.5")
    ksp("androidx.room:room-compiler:2.8.5")

    implementation("androidx.datastore:datastore-preferences:1.2.1")
    implementation("androidx.work:work-runtime-ktx:2.11.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

    implementation("com.squareup.retrofit2:retrofit:2.12.0")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.12.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("androidx.room:room-testing:2.8.5")
    testImplementation("androidx.test:core:1.7.0")
}
