import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.jetbrains.kotlin.serialization)

}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

val admobAppId = localProperties.getProperty("admob_app_id") ?: "ca-app-pub-3940256099942544~3347511713"
val admobRunDetailBannerId = localProperties.getProperty("admob_run_detail_screen_top_banner_id") ?: "ca-app-pub-3940256099942544/6300978111"
val admobHomeBannerId = localProperties.getProperty("admob_home_screen_bottom_banner_id") ?: "ca-app-pub-3940256099942544/6300978111"

android {
    namespace = "com.misterjerry.runningtracker"
    compileSdk = 36 // Targeting stable

    defaultConfig {
        applicationId = "com.misterjerry.runningtracker"
        minSdk = 26 // Android 8.0 for notification channels/foreground service goodies
        targetSdk = 35
        versionCode = 2
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        manifestPlaceholders["admob_app_id"] = admobAppId
        buildConfigField("String", "ADMOB_RUN_DETAIL_SCREEN_TOP_BANNER_ID", "\"$admobRunDetailBannerId\"")
        buildConfigField("String", "ADMOB_RUN_SCREEN_BOTTOM_BANNER_ID", "\"$admobHomeBannerId\"")
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

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.fragment.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)


    // Maps & Location
    implementation(libs.osmdroid.android)
    implementation(libs.play.services.location)
    implementation(libs.kotlinx.coroutines.play.services)
    
    // Ads
    implementation(libs.play.services.ads)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.google.gson)
    ksp(libs.androidx.room.compiler)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    //nav3
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.material3.adaptive.navigation3)
    implementation(libs.kotlinx.serialization.core)
}
