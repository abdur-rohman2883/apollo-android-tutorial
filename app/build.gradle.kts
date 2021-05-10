plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.apollographql.apollo").version("2.5.6")
}

android {
    compileSdk = 30

    defaultConfig {
        applicationId = "com.example.rocketreserver"
        minSdk = 23
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.0-beta06"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.compose.ui:ui:1.0.0-beta06")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.activity:activity-compose:1.3.0-alpha07")
    implementation("androidx.compose.foundation:foundation:1.0.0-beta06")
    // Material Design
    implementation("androidx.compose.material:material:1.0.0-beta06")
    // Material design icons
    implementation("androidx.compose.material:material-icons-core:1.0.0-beta06")
    implementation("androidx.compose.material:material-icons-extended:1.0.0-beta06")
    implementation("com.google.accompanist:accompanist-coil:0.9.1")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.0-alpha05")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha03")

    implementation("com.apollographql.apollo:apollo-runtime:2.5.6")
    implementation("com.apollographql.apollo:apollo-coroutines-support:2.5.6")
    implementation("androidx.compose.ui:ui-tooling:1.0.0-beta06")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}

apollo {
    generateKotlinModels.set(true)
}