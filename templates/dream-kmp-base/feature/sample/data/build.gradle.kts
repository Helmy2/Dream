plugins {
    alias(libs.plugins.kmpFeatureData)
}

dependencies {
    commonMainImplementation(projects.feature.sample.domain)
    commonMainImplementation(projects.shared.core.data)
    commonMainImplementation(projects.shared.core.domain)
    commonMainImplementation(projects.shared.core.storage)
}
