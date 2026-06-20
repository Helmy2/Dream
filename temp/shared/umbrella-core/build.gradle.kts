plugins {
    alias(libs.plugins.kmpSharedCore)
}

dependencies {
    commonMainApi(projects.feature.sample.data)
    commonMainApi(projects.feature.sample.domain)
    commonMainApi(projects.shared.core.data)
    commonMainApi(projects.shared.core.domain)
    commonMainImplementation(libs.koin.annotations)
    commonMainImplementation(libs.koin.core)
    commonMainImplementation(libs.kotlinx.coroutines.core)
    commonTestImplementation(kotlin("test"))
}
