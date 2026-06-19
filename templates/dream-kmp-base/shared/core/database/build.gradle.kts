plugins {
    alias(libs.plugins.kmpSharedCore)
}

dependencies {
    commonMainApi(libs.androidx.room.runtime)
    commonMainImplementation(libs.androidx.sqlite.bundled)
    commonMainImplementation(libs.kotlinx.coroutines.core)
}