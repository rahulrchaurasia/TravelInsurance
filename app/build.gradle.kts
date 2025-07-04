    plugins {
//    alias(libs.plugins.android.application)
//    alias(libs.plugins.kotlin.android)
//    alias(libs.plugins.ksp)
//    id("kotlin-parcelize")
//    id("androidx.navigation.safeargs.kotlin")
//    id("com.google.dagger.hilt.android")

        alias(libs.plugins.android.application)
        alias(libs.plugins.kotlin.android)
        alias(libs.plugins.ksp)
        alias(libs.plugins.hilt)
        alias(libs.plugins.navigation.safeargs)
        alias(libs.plugins.compose.compiler) // ✅ Add this for compose
        id("kotlin-parcelize")
}

android {
    namespace = "com.interstellar.travelInsurance"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.interstellar.travelInsurance"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        viewBinding = true
        buildConfig = true
        compose = true    //Enable Compose features
    }

}

dependencies {

    // AndroidX Core and UI
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // Activity and Fragment
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.paging.runtime)
    //If we used KTX than used it
//    implementation(libs.androidx.biometric.ktx)
    ksp(libs.androidx.room.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)

    // Navigation
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Coil
    implementation(libs.coil)

    //Image Cropper
    implementation(libs.image.cropper)

    //ViewPager Dot Indicator: com.tbuonomo:dotsindicator:5.0
    implementation(libs.dots.indicator)

    //biometric
    implementation(libs.androidx.biometric)


    // Core Compose libraries
    implementation(platform(libs.androidx.compose.bom)) // composeBom = "2024.06.00"
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.foundation)
//    implementation(libs.androidx.material)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime)

    // Compose tooling for previews and inspecting Composables during development
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)

    // Integration with Activity/Fragment for Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.fragment.compose)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}