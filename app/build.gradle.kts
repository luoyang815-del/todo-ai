
plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.plugin.compose")
}

android {
  namespace = "com.aihelper.app"
  compileSdk = 35
  defaultConfig {
    applicationId = "com.aihelper.app"
    minSdk = 24
    targetSdk = 35
    versionCode = 280
    versionName = "2.8.0"
  }
  buildTypes {
    release { isMinifyEnabled = false }
    debug { applicationIdSuffix = ".debug"; versionNameSuffix = "-debug" }
  }
  compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
  kotlinOptions { jvmTarget = "17" }
  buildFeatures { compose = true }
  composeOptions { kotlinCompilerExtensionVersion = "1.6.10" }
  packaging { resources { excludes += "/META-INF/{AL2.1,LGPL2.1}" } }
}

dependencies {
  val composeBom = platform("androidx.compose:compose-bom:2024.11.00")
  implementation(composeBom); androidTestImplementation(composeBom)
  implementation("androidx.core:core-ktx:1.15.0")
  implementation("androidx.activity:activity-compose:1.9.3")
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-tooling-preview")
  debugImplementation("androidx.compose.ui:ui-tooling")
  implementation("androidx.compose.material3:material3:1.3.1")
  implementation("com.squareup.retrofit2:retrofit:2.11.0")
  implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
  implementation("com.squareup.okhttp3:okhttp:4.12.0")
  implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
  implementation("com.squareup.moshi:moshi-kotlin:1.15.1")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
}
