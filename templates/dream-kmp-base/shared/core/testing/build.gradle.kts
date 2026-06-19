plugins {
    alias(libs.plugins.kmpSharedCore)
}

dependencies {
    commonMainImplementation(projects.shared.core.data)
    commonMainImplementation(projects.shared.core.domain)
    commonMainImplementation(libs.kotlinx.coroutines.core)
    commonMainImplementation(libs.kotlinx.coroutines.test)
}
