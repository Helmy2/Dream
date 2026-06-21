plugins {
    alias(libs.plugins.kmpFeatureDomain)
}

dependencies {
    commonMainImplementation(projects.shared.core.domain)
    commonTestImplementation(kotlin("test"))
    commonTestImplementation(libs.kotlinx.coroutines.test)
}
