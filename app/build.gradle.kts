plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "com.example.otpdetector"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.otpdetector"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    signingConfigs {
        create("release") {
            val keyStorePath = System.getenv("SIGNING_KEY_STORE_PATH") ?: "release.keystore"
            storeFile = file(keyStorePath)
            storePassword = System.getenv("SIGNING_STORE_PASSWORD") ?: "android"
            keyAlias = System.getenv("SIGNING_KEY_ALIAS") ?: "android"
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD") ?: "android"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }
}

dependencies {
    compileOnly(libs.xposed.api)
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
}
