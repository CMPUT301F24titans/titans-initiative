plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.titans_project"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.titans_project"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Android Libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)

    // Test Libraries
    testImplementation(libs.junit) // JUnit for testing
    testImplementation("org.robolectric:robolectric:4.8") // Robolectric for Android unit tests
    testImplementation("org.mockito:mockito-core:4.6.1") // Mockito for mocking dependencies

    // Android Test Libraries
    androidTestImplementation(libs.ext.junit) // JUnit for instrumentation tests
    androidTestImplementation(libs.espresso.core) // Espresso for UI testing

    // Firebase BoM (platform version for Firebase SDKs)
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))

    // ZXing library for QR code generation
    implementation("com.google.zxing:core:3.5.0")  // ZXing for QR code generation
}
