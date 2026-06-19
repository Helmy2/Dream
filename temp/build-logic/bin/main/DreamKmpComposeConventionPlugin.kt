import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.compose.resources.ResourcesExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class DreamKmpComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("app.kmp")
            pluginManager.apply("org.jetbrains.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            val modulePackage = "com.dream.template.${target.path.trim(':').replace(':', '.')}"

            extensions.configure<org.jetbrains.compose.ComposeExtension> {
                (this as ExtensionAware).extensions.configure<ResourcesExtension>("resources") {
                    packageOfResClass = modulePackage
                }
            }

            extensions.configure<KotlinMultiplatformExtension> {
                android {
                    androidResources.enable = true
                }
            }
        }
    }

    private fun KotlinMultiplatformExtension.android(
        configure: KotlinMultiplatformAndroidLibraryExtension.() -> Unit
    ) {
        (this as ExtensionAware).extensions.configure("android", configure)
    }
}
