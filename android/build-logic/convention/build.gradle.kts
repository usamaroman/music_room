import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.accessors.runtime.extensionOf

plugins {
    `kotlin-dsl`
    alias(libs.plugins.buildConfig)
}

dependencies {
    implementation(libs.bundles.gradle.plugins)
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

gradlePlugin {
    plugins {
        register("android-lib") {
            id = "android-lib"
            implementationClass = "AndroidLibraryPlugin"
            version = "1.0.0"
        }
    }
    plugins {
        register("android-app") {
            id = "android-app"
            implementationClass = "AndroidAppPlugin"
            version = "1.0.0"
        }
    }

    plugins {
        register("tech-change-folders") {
            id = "tech-change-folders"
            implementationClass = "ChangeFoldersPlugin"
            version = "1.0.0"
        }
    }
}

tasks.register("hello") {
    doLast {
        println("Hello world!")
    }
}

public val Project.libs
    get(): LibrariesForLibs = extensionOf(this, "libs") as LibrariesForLibs