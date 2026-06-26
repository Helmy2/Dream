plugins {
    alias(libs.plugins.kmpSharedCore)
    alias(libs.plugins.androidxRoom)
    alias(libs.plugins.ksp)
}

dependencies {
    commonMainApi(libs.androidx.room.runtime)
    commonMainImplementation(libs.androidx.sqlite.bundled)
    commonMainImplementation(libs.kotlinx.coroutines.core)
    commonMainImplementation(projects.shared.core.domain)

    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}