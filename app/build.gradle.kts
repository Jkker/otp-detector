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
            // 1. Define the Standard Location
            val homeDir = System.getProperty("user.home")
            val keyStoreFile = File("$homeDir/.config/android-signing/release.jks")

            // 2. Load passwords from Global Gradle Properties (~/.gradle/gradle.properties)
            // Gradle automatically loads these into the project scope!
            val keyStorePass = project.findProperty("ORG_KEYSTORE_PASS") as? String
            val keyAliasName = project.findProperty("ORG_KEY_ALIAS") as? String
            val keyAliasPass = project.findProperty("ORG_KEY_ALIAS_PASS") as? String

            if (keyStoreFile.exists() && keyStorePass != null) {
                storeFile = keyStoreFile
                storePassword = keyStorePass
                keyAlias = keyAliasName
                keyPassword = keyAliasPass
            } else {
                println("⚠️ Release Keystore not found at $keyStoreFile. Skipping signing config.")
            }
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
