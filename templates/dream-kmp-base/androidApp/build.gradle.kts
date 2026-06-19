plugins {
    alias(libs.plugins.kmpAndroidApp)
}

android {
    namespace = "com.dream.template.android"

    defaultConfig {
        applicationId = "com.dream.template.android"
    }
}

dependencies {
    implementation(projects.shared.umbrellaCore)
    implementation(projects.shared.umbrellaUi)
}
