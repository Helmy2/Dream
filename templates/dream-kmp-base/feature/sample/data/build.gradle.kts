plugins {
    alias(libs.plugins.kmpFeatureData)
    alias(libs.plugins.androidxRoom)
    alias(libs.plugins.koinCompiler)
    alias(libs.plugins.ksp)
}

dependencies {
    commonMainImplementation(projects.feature.sample.domain)
    commonMainImplementation(projects.shared.core.database)
    commonMainImplementation(projects.shared.core.domain)
    commonMainImplementation(projects.shared.core.storage)
    commonMainImplementation(libs.koin.annotations)
    commonMainImplementation(libs.koin.core)

    add("kspAndroid", libs.androidx.room.compiler)
    add("kspDesktop", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}
