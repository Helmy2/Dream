plugins {
    alias(libs.plugins.kmpCompose)
}

dependencies {
    commonMainImplementation(projects.feature.sample.data)
    commonMainImplementation(projects.feature.sample.domain)
    commonMainImplementation(projects.shared.core.presentation)
    commonMainApi(projects.shared.designsystem)
    commonMainApi(projects.shared.navigation)
    commonMainApi(projects.feature.sample.presentation)
    commonMainImplementation(libs.compose.runtime)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            export(projects.shared.designsystem)
            export(projects.shared.navigation)
            export(projects.feature.sample.presentation)
        }
    }
}
