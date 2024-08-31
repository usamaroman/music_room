package config


import org.gradle.api.JavaVersion

object Config {
    const val applicationId = "by.eapp.musicroom"
    const val versionName = "1.0"
    const val compileSdkVersion = 34
    const val buildToolsVersion = "34.0.0"
    const val minSdkVersion = 24
    const val targetSdkVersion = 34
    val jvmTarget = JavaVersion.VERSION_17.toString()
    const val ndkVersion = "21.1.6352462"
}
