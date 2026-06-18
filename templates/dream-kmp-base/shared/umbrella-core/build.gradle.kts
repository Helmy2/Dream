plugins {
    alias(libs.plugins.kmpSharedCore)
}

dependencies {
    commonMainApi(projects.feature.sample.domain)
    commonMainApi(projects.shared.core.domain)
}
