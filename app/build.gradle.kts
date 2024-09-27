plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    id("com.google.gms.google-services") // Add the Google services plugin here
}

android {
    namespace = "com.example.practiceapplicationbrg"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.practiceapplicationbrg"
        minSdk = 32
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.cronet.embedded)
    implementation(libs.firebase.firestore)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("com.google.android.material:material:1.4.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.0")
    implementation ("androidx.appcompat:appcompat:1.3.1")

    // Use the BOM for Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:32.2.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation ("com.google.android.gms:play-services-auth:20.5.0")

    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // Add Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0") // Core Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0") // Android-specific Coroutines


    implementation(libs.retrofit)
    implementation(libs.gsonConverter)
    implementation(libs.kotlinCoroutines)

    // Add your generative AI dependency
    implementation ("com.google.ai.client.generativeai:generativeai:0.1.1")

    implementation ("androidx.cardview:cardview:1.0.0")



}

// Apply the Google Services plugin
apply(plugin = "com.google.gms.google-services")