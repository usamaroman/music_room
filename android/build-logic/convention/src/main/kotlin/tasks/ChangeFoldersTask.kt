package tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class ChangeFoldersTask : DefaultTask() {

    companion object {
        private const val SRC_PACKAGE = "by.music"
        private const val PROJECT_PATH = "src/main/kotlin/"
    }

    @TaskAction
    fun changeFolders() {
        with(project) {

            val sourceDir = File(
                projectDir,
                PROJECT_PATH.plus(SRC_PACKAGE.replace(".", "/")),
            )


            val newPackage = findProperty("projectPackage")?.toString()
                ?: error("Not found 'projectPackage'")

            val targetDir = File(
                projectDir,
                PROJECT_PATH.plus(newPackage.replace(".", "/")),
            )


            if (sourceDir.exists()) {

                runCatching { sourceDir.copyRecursively(targetDir) }

                runCatching { updatePackageNames(targetDir, SRC_PACKAGE, newPackage) }

                SRC_PACKAGE.split(".")
                    .firstOrNull()
                    ?.let { File(projectDir, PROJECT_PATH.plus(it)) }
                    ?.deleteRecursively()

                runCatching { commitChanges() }
            }
        }
    }

    private fun commitChanges() {
        val gitAddCommand = "git add ."

        ProcessBuilder(gitAddCommand.split(" "))
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
            .waitFor()
    }

    private fun updatePackageNames(directory: File, oldPackage: String, newPackage: String) {
        val files = directory.listFiles()
        files?.forEach { file ->
            if (file.isDirectory) {
                updatePackageNames(file, oldPackage, newPackage)
            } else {
                val content = file.readText()
                val updatedContent = content
                    .replace("package $oldPackage", "package $newPackage")
                    .replace("import $oldPackage", "import $newPackage")
                file.writeText(updatedContent)
            }
        }
    }
}
