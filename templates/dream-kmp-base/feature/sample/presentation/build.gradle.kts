plugins {
    alias(libs.plugins.kmpFeaturePresentation)
}

dependencies {
    commonMainImplementation(projects.feature.sample.domain)
    commonMainImplementation(projects.feature.sample.data)
    commonMainImplementation(projects.shared.core.presentation)
    commonMainImplementation(projects.shared.designsystem)
    commonMainImplementation(libs.compose.runtime)
    commonMainImplementation(libs.compose.foundation)
    commonMainImplementation(libs.compose.material3)
    commonMainImplementation(libs.compose.ui)
    commonTestImplementation(kotlin("test"))
}
