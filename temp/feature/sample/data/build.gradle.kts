plugins {
    alias(libs.plugins.kmpFeatureData)
    alias(libs.plugins.koinCompiler)
}

dependencies {
    commonMainImplementation(projects.feature.sample.domain)
    commonMainImplementation(projects.shared.core.data)
    commonMainImplementation(projects.shared.core.domain)
    commonMainImplementation(libs.koin.core)
    commonTestImplementation(projects.shared.core.testing)
    commonTestImplementation(kotlin("test"))
    commonTestImplementation(libs.kotlinx.coroutines.test)
}
