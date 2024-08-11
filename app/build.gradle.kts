plugins {
    alias(libs.plugins.android.application)
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.rodrigotriboni.budget"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.rodrigotriboni.budget"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "geminiApiKey", "\"${project.findProperty("GEMINI_API_KEY")}\"")
    }
    buildTypes {
        buildTypes {
            release {
                isMinifyEnabled = false
                buildConfigField("String", "geminiApiKey", "\"${project.findProperty("GEMINI_API_KEY")}\"")
            }
            debug {
                buildConfigField("String", "geminiApiKey", "\"${project.findProperty("GEMINI_API_KEY")}\"")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true

    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.auth)
    implementation(libs.core.ktx)
    implementation(libs.play.services.nearby)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("me.relex:circleindicator:2.1.6")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("com.google.firebase:firebase-database:21.0.0")
    implementation ("com.google.firebase:firebase-storage:21.0.0")
    implementation("com.google.firebase:firebase-appcheck-playintegrity:18.0.0")

    // add the dependency for the Google AI client SDK for Android
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    implementation("com.google.guava:guava:31.0.1-android")

    implementation("org.reactivestreams:reactive-streams:1.0.4")

    implementation ("com.itextpdf:itextg:5.5.10")

    implementation ("androidx.security:security-crypto:1.1.0-alpha05")

    implementation ("com.google.code.gson:gson:2.8.8")


}