plugins {
    alias(libs.plugins.android.application)
}

// Khối này sẽ ép buộc Gradle sử dụng đúng phiên bản thư viện
configurations.all {
    resolutionStrategy {
        force("mysql:mysql-connector-java:5.1.49")
    }
}

android {
    namespace = "com.example.app_dat_lich_kham_benh"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.app_dat_lich_kham_benh"
        minSdk = 24
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
        isCoreLibraryDesugaringEnabled = true
    }
    packagingOptions {
        exclude("META-INF/services/javax.xml.stream.XMLEventFactory")
        exclude("META-INF/NOTICE.md")
        exclude("META-INF/LICENSE.md")
    }
}

dependencies {
    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("mysql:mysql-connector-java:5.1.49") // Vẫn giữ lại ở đây
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}