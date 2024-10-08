/*
 *  No password for you
 *  Copyright (c) 2024 . All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation,either version 3 of the License,or
 * (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not,see <http://www.gnu.org/licenses/>.
 */

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")

    kotlin("kapt")
    id("androidx.room")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.leo.nopasswordforyou"
    compileSdk = 34

    buildFeatures {
        compose = true
        viewBinding = true
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    defaultConfig {
        applicationId = "com.leo.nopasswordforyou"
        minSdk = 23
        targetSdk = 34
        versionCode = 3
        versionName = "2.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions.freeCompilerArgs.addAll(
        "-P",
        "plugin:androidx.compose.compiler.plugins.kotlin:strongSkipping=true",
    )
}

dependencies {


    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.biometric)
    //  implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.coil.compose)




    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.material3.android)

    implementation(libs.material)



    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.lifecycle.runtime.ktx)


    implementation(platform(libs.firebase.bom))

    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)


    implementation(libs.bcpkix.jdk18on)
    implementation(libs.gson)

    implementation(libs.core)



    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    annotationProcessor(libs.androidx.room.compiler)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    ksp(libs.androidx.room.compiler)

    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.hilt.android.compiler)

    implementation(libs.snowfall)
}



