import org.gradle.api.Plugin
import org.gradle.api.Project

class DreamKmpFeaturePresentationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("app.kmp.compose")
        }
    }
}
