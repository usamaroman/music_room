@file:Suppress("unused")

package settings

import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import config.Config
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType

internal typealias AndroidBaseAppExtension = BaseAppModuleExtension

internal fun Project.androidApplicationSetup() =
    this.extensions.getByType<AndroidBaseAppExtension>().run {


        compileSdk = Config.compileSdkVersion
        buildToolsVersion = Config.buildToolsVersion

        defaultConfig {
            versionCode = calculateVersionCode()
            versionName = Config.versionName
            minSdk = Config.minSdkVersion
            targetSdk = Config.targetSdkVersion
            applicationId = Config.applicationId

            vectorDrawables {
                useSupportLibrary = true
            }
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
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17

        }

        buildFeatures {
            buildFeatures.buildConfig = true
            buildFeatures.compose = true
        }

        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }

        applicationVariants.all {
            outputs
                .mapNotNull { it as? BaseVariantOutputImpl }
                .forEach { output ->
                    val pattern = findProperty("apkPattern")?.toString()
                        ?: error("Not Found 'Apk Pattern'")

                    val projectName = findProperty("projectName")?.toString()
                        ?: error("Not Found 'Apk projectName'")

                    val newApkName = pattern
                        .replace("\$projectName", "-$projectName")
                        .replace("\$versionName", "-$versionName")
                        .replace("\$versionCode", "-$versionCode")
                        .replace(
                            "\$flavorName",
                            "-$flavorName".takeIf { flavorName.isNotEmpty() }.orEmpty()
                        )
                        .replace("\$buildType", if (name.contains("debug", true)) "-debug" else "")

                    output.outputFileName =
                        if (newApkName.startsWith("-")) newApkName.substring(1)
                        else newApkName
                }
        }

        sourceSets["main"].res.srcDirs("src/commonMain/resources", "src/androidMain/res")
        sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    }

private fun Project.calculateVersionCode(): Int {
    return runCatching {

        val gitLogCommand = "git rev-list --count HEAD"
        val process = ProcessBuilder(gitLogCommand.split(" "))
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        process.inputStream
            .bufferedReader()
            .use { it.readLine().trim().toInt() }

    }.getOrElse {

        println("WARNING: Not found git commits, checking local Version Code")

        findProperty("localVersionCode")?.toString()?.toIntOrNull()
            ?: error("Not Found 'localVersionCode'")
    }
}
