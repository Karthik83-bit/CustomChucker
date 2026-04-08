/*
 * API Tracker SDK - Android Library
 * 
 * MINIMUM VERSION REQUIREMENTS FOR CONSUMER APPS:
 * - Kotlin: 2.0.0 or higher (CRITICAL - breaks with 1.9.x)
 * - Android Gradle Plugin (AGP): 8.4.2 or higher
 * - Gradle: 8.4 or higher
 * - Compose BOM: 2024.06.00 or higher
 * - compileSdk: 34 or higher
 * - minSdk: 21 or higher
 * 
 * Hilt is REQUIRED and exposed as API dependency
 * See README.md for complete integration guide
 */

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
//    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)  // Hilt dependency injection
    alias(libs.plugins.ksp)
    `maven-publish`
}

android {
    namespace = "com.isu.apitracker"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

publishing {
    /*
    * ./gradlew publishToMavenLocal
    * Use this command to publish the library
    * */
    publications {

        create<MavenPublication>("mavenAar") {
            groupId = "com.isu"
            artifactId = "apitracker"
//            version = "1.0.0_stag"  // Staging
            version = "1.0.0"  // Production

            afterEvaluate {
                from(components["release"])
            }
        }
    }

    repositories {
        maven {
            url = uri("file://${layout.buildDirectory}/repo")
        }
    }

}




dependencies {
    // Publish Compose dependencies transitively because the SDK's UI layer uses them at runtime.
    api(platform(libs.androidx.compose.bom))
    // ===== CORE ANDROID & COMPOSE (Consumer must have compatible versions) =====
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.navigation.compose)

    // ===== DEPENDENCY INJECTION - HILT (EXPOSED API) =====
    // SDK uses Hilt for DI. Consumer app MUST also configure Hilt.
    // Consumer must:
    // 1. Add Hilt plugin: id("com.google.dagger.hilt.android")
    // 2. Add Hilt dependencies: hilt-android + hilt-android-compiler
    // 3. Annotate Application class: @HiltAndroidApp
    api(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    api(libs.hilt.navigation.compose)  // For navigation with Hilt

    // ===== LOCAL STORAGE (Room Database) =====
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.runtime.livedata)  // LiveData in Compose

    // ===== NETWORKING (EXPOSED PUBLIC APIs) =====
    // These versions are part of SDK's contract
    // If consumer has different versions, use resolutionStrategy to manage conflicts
    api(libs.retrofit2)              // 2.11.0 - HTTP client
    api(libs.gson.converter)         // 2.11.0 - JSON parsing
    api(libs.logging.interceptor)    // 5.0.0-alpha.11 - Request/response logging

    // ===== TESTING =====
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
//    debugImplementation(libs.androidx.ui.test.manifest)

}

tasks {
    withType<PublishToMavenLocal>().configureEach {
        dependsOn("assemble")
    }
}
