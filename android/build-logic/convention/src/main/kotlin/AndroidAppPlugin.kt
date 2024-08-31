import org.gradle.api.Plugin
import org.gradle.api.Project
import settings.androidApplicationSetup


open class AndroidAppPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.androidApplicationSetup()
    }
}