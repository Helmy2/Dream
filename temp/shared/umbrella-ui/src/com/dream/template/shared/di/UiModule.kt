package com.dream.template.shared.di

import com.dream.template.shared.home.HomeRoute
import com.dream.template.shared.home.placeholderHomeModule
import com.dream.template.shared.navigation.Navigator
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module


val navigatorModule = module {
    single { Navigator(startDestination = HomeRoute) }
}

private var hasStartedKoin = false

/**
 * Composition root. Feature modules are listed in [startKoin]'s `modules(...)` call: add each feature's
 * data module and UI module here, and give [initKoin] whatever platform factories they need.
 */
fun initKoin(
    appContext: Any? = null,
    appDeclaration: KoinAppDeclaration = {}
) {
    if (hasStartedKoin) return

    startKoin {
        appDeclaration()
        modules(
            coreModule(appContext),
            placeholderHomeModule,
            navigatorModule,
        )
    }
    hasStartedKoin = true
}
