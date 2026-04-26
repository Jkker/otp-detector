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

    val releaseTaskRequested =
        gradle.startParameter.taskNames.any { it.contains("Release", ignoreCase = true) }

    val keystoreFile = providers.environmentVariable("ANDROID_KEYSTORE_FILE").orNull
    val keystorePassword = providers.environmentVariable("ANDROID_KEYSTORE_PASSWORD").orNull
    val keyAliasValue = providers.environmentVariable("ANDROID_KEY_ALIAS").orNull
    val keyPasswordValue = providers.environmentVariable("ANDROID_KEY_PASSWORD").orNull
    val keystoreTypeValue = providers.environmentVariable("ANDROID_KEYSTORE_TYPE").orNull

    val hasReleaseSigning =
        !keystoreFile.isNullOrBlank() &&
        !keystorePassword.isNullOrBlank() &&
        !keyAliasValue.isNullOrBlank() &&
        !keyPasswordValue.isNullOrBlank()

    if (hasReleaseSigning) {
        signingConfigs {
            create("releaseFromEnv") {
                storeFile = file(keystoreFile)
                storePassword = keystorePassword
                keyAlias = keyAliasValue
                keyPassword = keyPasswordValue

                if (!keystoreTypeValue.isNullOrBlank()) {
                    storeType = keystoreTypeValue
                }
            }
        }

        buildTypes {
            getByName("release") {
                signingConfig = signingConfigs.getByName("releaseFromEnv")
            }
        }
    } else if (releaseTaskRequested) {
        throw GradleException(
            "Release signing env vars missing. Run `mise build`, not `./gradlew assembleRelease` directly."
        )
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
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
