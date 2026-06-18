plugins {
    alias(libs.plugins.kmpSharedCore)
}

dependencies {
    commonMainImplementation(projects.shared.core.domain)
    commonMainImplementation(libs.kotlinx.coroutines.core)
}
