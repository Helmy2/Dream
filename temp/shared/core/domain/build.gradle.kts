plugins {
    alias(libs.plugins.kmpSharedCore)
}

dependencies {
    commonMainImplementation(libs.kotlinx.coroutines.core)
    commonTestImplementation(kotlin("test"))
}
