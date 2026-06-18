plugins {
    alias(libs.plugins.kmpCompose)
}

dependencies {
    commonMainImplementation(projects.shared.designsystem)
    commonMainImplementation(libs.compose.material3)
    commonMainImplementation(libs.navigation.compose)
}
