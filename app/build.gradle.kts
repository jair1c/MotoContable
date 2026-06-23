plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

import java.util.Properties
import java.io.File

// ═══════════════════════════════════════════════════════════════════
// LEER CREDENCIALES DE local.properties
// ═══════════════════════════════════════════════════════════════════
val keystoreProperties = Properties()
val keystoreFile = rootProject.file("local.properties")
if (keystoreFile.exists()) {
    keystoreFile.inputStream().use { keystoreProperties.load(it) }
}

android {
    namespace   = "com.motocontable.app"
    compileSdk  = 35

    defaultConfig {
        applicationId = "com.motocontable.app"
        minSdk        = 26
        targetSdk     = 35
        versionCode   = 1
        versionName   = "1.0.0"
    }

    // ═══════════════════════════════════════════════════════════════════
    // CONFIGURACIÓN DE FIRMA (KEYSTORE)
    // ═══════════════════════════════════════════════════════════════════
    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties.getProperty("KEY_ALIAS")
            keyPassword = keystoreProperties.getProperty("KEY_PASSWORD").toCharArray()
            storeFile = file(keystoreProperties.getProperty("KEYSTORE_PATH"))
            storePassword = keystoreProperties.getProperty("KEYSTORE_PASSWORD").toCharArray()
        }
    }

    buildTypes {
        debug {
            // Debug también firmado (para que sea consistente)
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    debugImplementation(libs.androidx.ui.tooling)
}
