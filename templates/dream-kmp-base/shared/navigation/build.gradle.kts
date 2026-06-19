plugins {
    alias(libs.plugins.kmpCompose)
    alias(libs.plugins.koinCompiler)
}

dependencies {
    commonMainImplementation(projects.shared.designsystem)
    commonMainImplementation(projects.shared.core.presentation)
    commonMainImplementation(libs.koin.compose)
    commonMainImplementation(libs.koin.composeNavigation3)
    commonMainImplementation(libs.compose.material3)
    commonMainImplementation(libs.navigation3.runtime)
    commonMainImplementation(libs.navigation3.ui)
    commonTestImplementation(kotlin("test"))
}
