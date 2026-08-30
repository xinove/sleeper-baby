plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.sleeperbaby.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.sleeperbaby.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 4
        versionName = "1.0.3"

        manifestPlaceholders["admobAppId"] = "ca-app-pub-1500150166852996~1158458948"
    }

    buildTypes {
        debug {
            manifestPlaceholders["admobAppId"] = "ca-app-pub-3940256099942544~3347511713"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            ndk {
                // Solo ARM: Play deja de generar artefactos x86/x86_64 que inflan la descarga.
                abiFilters += listOf("armeabi-v7a", "arm64-v8a")
                debugSymbolLevel = "SYMBOL_TABLE"
            }
            manifestPlaceholders["admobAppId"] = "ca-app-pub-1500150166852996~1158458948"
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
        buildConfig = true
    }

    lint {
        // Android Studio writes unescaped Windows paths in local.properties.
        disable += "PropertyEscape"
    }

    bundle {
        abi { enableSplit = true }
        density { enableSplit = true }
        language { enableSplit = true }
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.fragment:fragment-ktx:1.8.8")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.media:media:1.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("com.google.android.gms:play-services-ads:25.4.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
}

tasks.register<Zip>("packageReleaseNativeDebugSymbols") {
    val aab = layout.buildDirectory.file("outputs/bundle/release/app-release.aab")
    inputs.file(aab)
    archiveFileName.set("native-debug-symbols.zip")
    destinationDirectory.set(layout.buildDirectory.dir("outputs/native-debug-symbols/release"))
    from({ zipTree(aab) }) {
        include("base/lib/**/*.so")
        eachFile {
            path = relativePath.pathString.replace('\\', '/').removePrefix("base/lib/")
        }
        includeEmptyDirs = false
    }
}

tasks.matching { it.name == "bundleRelease" }.configureEach {
    finalizedBy("packageReleaseNativeDebugSymbols")
}
