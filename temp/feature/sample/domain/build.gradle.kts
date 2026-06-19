plugins {
    alias(libs.plugins.kmpFeatureDomain)
}

dependencies {
    commonMainImplementation(projects.shared.core.domain)
}
