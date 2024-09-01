plugins {
    id("com.android.library")
    id("android-lib")
}

android {
    namespace = "by.music.authorization"
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:storage"))
    implementation(project(":core:ui"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}