plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.clocker"
    compileSdk = 34  // ✅ CAMBIO: Bajamos de 36 a 34

    defaultConfig {
        applicationId = "com.example.clocker"
        minSdk = 24  // ✅ CAMBIO: Bajamos de 33 a 24 para mayor compatibilidad
        targetSdk = 34  // ✅ CAMBIO: Bajamos de 36 a 34
        versionCode = 1
        versionName = "1.0"

        // ✅ AGREGAR: Multidex
        multiDexEnabled = true

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // ✅ AndroidX Core - VERSIÓN ESPECÍFICA QUE FUNCIONA
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.8.2")

    // ✅ Material Design - VERSIÓN COMPATIBLE
    implementation("com.google.android.material:material:1.11.0")

    // ✅ QUITAR LAS REFERENCIAS DE libs.plugins si causan conflicto
    // implementation(libs.androidx.core.ktx)
    // implementation(libs.androidx.appcompat)
    // etc.

    // AndroidX adicionales (si las necesitas)
    // implementation(libs.androidx.material3)
    // implementation(libs.androidx.core.i18n)
    // implementation(libs.androidx.ui.text)
    // implementation(libs.protolite.well.known.types)

    // Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Coroutines para Firebase
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    // Multidex
    implementation("androidx.multidex:multidex:2.0.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}