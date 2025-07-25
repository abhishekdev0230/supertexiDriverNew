import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("dev.flutter.flutter-gradle-plugin")
    id("com.google.gms.google-services")
}

// Load keystore.properties
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        load(FileInputStream(keystorePropertiesFile))
    } else {
        println("⚠️ Warning: keystore.properties file not found.")
    }
}

android {
    namespace = "com.supertaxi.driverapp"
    compileSdk = 35
    ndkVersion = "27.0.12077973"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    defaultConfig {
        applicationId = "com.supertaxi.driverapp"
        minSdk = 23
        targetSdk = 35
        versionCode = flutter.versionCode
        versionName = flutter.versionName
    }

    signingConfigs {
        create("release") {
            val keyAliasValue = keystoreProperties["keyAlias"]?.toString()
            val keyPasswordValue = keystoreProperties["keyPassword"]?.toString()
            val storeFileValue = keystoreProperties["storeFile"]?.toString()
            val storePasswordValue = keystoreProperties["storePassword"]?.toString()

            if (keyAliasValue != null &&
                    keyPasswordValue != null &&
                    storeFileValue != null &&
                    storePasswordValue != null
            ) {
                keyAlias = keyAliasValue
                keyPassword = keyPasswordValue
                storeFile = project.file(storeFileValue)
                storePassword = storePasswordValue
            } else {
                println("❌ Error: Missing keystore properties. Aborting release signing.")
                // Optional: throw GradleException to fail early if keystore is required
                // throw GradleException("Missing keystore values in keystore.properties")
            }
        }
    }



    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
        }
    }
}

flutter {
    source = "../.."
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.4")
}
