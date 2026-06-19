import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class DreamKmpConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.multiplatform")
            pluginManager.apply("com.android.kotlin.multiplatform.library")

            val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()
            val coroutinesAndroid = libs.kotlinx.coroutines.android
            val coroutinesSwing = libs.kotlinx.coroutines.swing

            extensions.configure<KotlinMultiplatformExtension> {
                jvmToolchain(17)

                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }

                configureAndroid {
                    namespace = target.path
                        .trim(':')
                        .split(':')
                        .joinToString(separator = ".") { segment ->
                            segment.replace("-", "")
                        }
                        .let { "com.dream.template.$it" }
                    compileSdk = libs.versions.android.compileSdk.get().toInt()
                    minSdk = libs.versions.android.minSdk.get().toInt()
                }

                jvm("desktop")

                listOf(
                    iosArm64(),
                    iosSimulatorArm64()
                )

                sourceSets.getByName("androidMain").dependencies {
                    implementation(coroutinesAndroid)
                }

                sourceSets.getByName("desktopMain").dependencies {
                    implementation(coroutinesSwing)
                }
            }
        }
    }

    private fun KotlinMultiplatformExtension.configureAndroid(
        configure: KotlinMultiplatformAndroidLibraryExtension.() -> Unit
    ) {
        (this as ExtensionAware).extensions.configure("android", configure)
    }
}
