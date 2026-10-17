plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)

    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.10"

}

android {
    namespace = "com.example.merchantapp"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.merchantapp"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
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

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.ui.graphics)
    implementation("com.google.maps.android:maps-compose:4.3.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    // =========================================================
    // Unit Testing
    // =========================================================

    testImplementation(libs.junit)

    testImplementation("io.mockk:mockk:1.14.6")

    testImplementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2"
    )

    testImplementation(
        "app.cash.turbine:turbine:1.2.1"
    )


    // =========================================================
    // Android / Compose
    // =========================================================

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.compose.ui)

    implementation(libs.androidx.compose.ui.graphics)

    implementation(libs.androidx.compose.ui.tooling.preview)

    implementation(libs.androidx.compose.foundation)

    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.material3)

    implementation("androidx.compose.material:material-icons-extended")


    // =========================================================
    // Lifecycle
    // =========================================================

    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7"
    )


    // =========================================================
    // Navigation
    // =========================================================

    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.navigation.runtime.ktx)

    implementation(
        "androidx.hilt:hilt-navigation-compose:1.2.0"
    )


    // =========================================================
    // Paging
    // =========================================================

    implementation(
        "androidx.paging:paging-runtime:3.3.6"
    )

    implementation(
        "androidx.paging:paging-compose:3.3.6"
    )

    implementation(libs.androidx.paging.common)


    // =========================================================
    // Retrofit
    // =========================================================

    implementation(
        "com.squareup.retrofit2:retrofit:2.11.0"
    )

    implementation(
        "com.squareup.retrofit2:converter-gson:2.11.0"
    )


    // =========================================================
    // OkHttp
    // =========================================================

    implementation(
        "com.squareup.okhttp3:logging-interceptor:4.12.0"
    )


    // =========================================================
    // Hilt
    // =========================================================

    implementation(
        "com.google.dagger:hilt-android:2.60.1"
    )

    ksp(
        "com.google.dagger:hilt-compiler:2.60.1"
    )


    // =========================================================
    // Security
    // =========================================================

    implementation(
        "androidx.security:security-crypto:1.1.0-alpha06"
    )


    // =========================================================
    // Instrumentation / UI Testing
    // =========================================================

    androidTestImplementation(
        platform(libs.androidx.compose.bom)
    )

    androidTestImplementation(
        libs.androidx.compose.ui.test.junit4
    )

    androidTestImplementation(
        libs.androidx.espresso.core
    )

    androidTestImplementation(
        libs.androidx.junit
    )


    // =========================================================
    // Debug
    // =========================================================

    debugImplementation(
        libs.androidx.compose.ui.test.manifest
    )

    debugImplementation(
        libs.androidx.compose.ui.tooling
    )
}

