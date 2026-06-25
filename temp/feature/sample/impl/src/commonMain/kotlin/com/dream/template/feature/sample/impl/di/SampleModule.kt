package com.dream.template.feature.sample.impl.di

import com.dream.template.feature.sample.api.navigation.SampleDetailRoute
import com.dream.template.feature.sample.api.navigation.SampleRoute
import com.dream.template.feature.sample.impl.SamplePresentationMapper
import com.dream.template.feature.sample.impl.SampleScreen
import com.dream.template.feature.sample.impl.SampleViewModel
import com.dream.template.shared.navigation.Navigator
import org.koin.compose.koinInject
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation
import org.koin.plugin.module.dsl.viewModel


@OptIn(KoinExperimentalAPI::class)
val sampleUiModule = module {
    factory<SamplePresentationMapper> { SamplePresentationMapper(get()) }
    viewModel<SampleViewModel>()

    navigation<SampleRoute> {
        val navigator = koinInject<Navigator>()
        SampleScreen(
            onNavigateDetail = { id -> navigator.goTo(SampleDetailRoute(id)) },
            onNavigateBack = { navigator.goBack() },
        )
    }

    navigation<SampleDetailRoute> { route ->
        val navigator = koinInject<Navigator>()
        SampleScreen(
            selectedId = route.id,
            onNavigateDetail = { id -> navigator.goTo(SampleDetailRoute(id)) },
            onNavigateBack = { navigator.goBack() },
        )
    }
}
