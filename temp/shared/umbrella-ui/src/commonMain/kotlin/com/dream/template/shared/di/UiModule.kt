package com.dream.template.shared.di

import com.dream.template.feature.sample.data.local.SampleDatabaseFactory
import com.dream.template.feature.sample.di.sampleUiModule
import com.dream.template.shared.core.presentation.SampleRoute
import com.dream.template.shared.navigation.Navigator
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module


val navigatorModule = module {
    single { Navigator(startDestination = SampleRoute) }
}

fun initKoin(
    sampleDatabaseFactory: SampleDatabaseFactory? = null,
    appDeclaration: KoinAppDeclaration = {}
) {
    startKoin {
        appDeclaration()
        modules(sampleDataModule(sampleDatabaseFactory), sampleUiModule, navigatorModule)
    }
}