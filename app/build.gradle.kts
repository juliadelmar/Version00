plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.version00"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.version00"
        minSdk = 30
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(11))
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // ✅ ÚNICA versión de Compose: BOM 2023 estable
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation("androidx.compose.material:material")
    // Compose BOM (Bill of Materials)

    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose")
    implementation("androidx.navigation:navigation-compose:2.7.7")
// Vico chart completo (¡las 3 son necesarias!)
    implementation("com.patrykandpatrick.vico:core:2.1.2")
    implementation("com.patrykandpatrick.vico:compose:2.1.2")
    implementation("com.patrykandpatrick.vico:compose-m3:2.1.2") // Material 3
 // si usas Material3
    // Para los iconos básicos de Material Design
    implementation("androidx.compose.material:material-icons-core:1.6.7") // Usa la última versión compatible
    implementation(platform("androidx.compose:compose-bom:2024.12.00"))
    implementation("androidx.compose.material3:material3")
    // Opcional: Para un conjunto extendido de iconos de Material Design
    implementation("androidx.compose.material:material-icons-extended:1.6.7")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui-text")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")// O la última versión estable

    // Otros componentes esenciales
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    //noinspection UseTomlInstead

    // 🔧 Red limpia y única para Retrofit + Gson
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.google.code.gson:gson:2.11.0")

    // ✅ Coil solo una vez
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Glide y Picasso (usa solo uno en producción)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.kizitonwose.calendar:compose:2.3.0")

    // SVG a Compose (verifica compatibilidad con Compose BOM)

    // Firebase y autenticación
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.messaging)
    implementation(libs.volley)
    implementation("com.google.firebase:firebase-messaging:23.1.2")

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}