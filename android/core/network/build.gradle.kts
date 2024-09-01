plugins {
    id("com.android.library")
    id("android-lib")
}

android {
    namespace = "by.music.network"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    implementation (libs.retrofit)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp3.okhttp)
    implementation(libs.okhttp3.logging.interceptor)
    implementation (libs.converter.gson.v250)
    implementation(libs.converter.gson)
}