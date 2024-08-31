import org.gradle.api.Plugin
import org.gradle.api.Project
import settings.androidLibrarySetup


class AndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.androidLibrarySetup()
    }
}