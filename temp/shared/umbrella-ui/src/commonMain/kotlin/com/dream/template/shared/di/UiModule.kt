package com.dream.template.shared.di

import com.dream.template.feature.sample.api.navigation.SampleRoute
import com.dream.template.feature.sample.impl.di.sampleUiModule
import com.dream.template.shared.core.data.local.SampleDatabaseFactory
import com.dream.template.shared.navigation.Navigator
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module


val navigatorModule = module {
    single { Navigator(startDestination = SampleRoute) }
}

private var hasStartedKoin = false

fun initKoin(
    sampleDatabaseFactory: SampleDatabaseFactory,
    appContext: Any? = null,
    appDeclaration: KoinAppDeclaration = {}
) {
    if (hasStartedKoin) return

    startKoin {
        appDeclaration()
        modules(sampleDataModule(sampleDatabaseFactory, appContext), sampleUiModule, navigatorModule)
    }
    hasStartedKoin = true
}
