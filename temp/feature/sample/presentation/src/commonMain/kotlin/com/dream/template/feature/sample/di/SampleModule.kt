package com.dream.template.feature.sample.di

import com.dream.template.feature.sample.presentation.SampleScreen
import com.dream.template.feature.sample.presentation.SampleViewModel
import com.dream.template.feature.sample.presentation.navigation.SampleRoute
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation
import org.koin.plugin.module.dsl.viewModel


@OptIn(KoinExperimentalAPI::class)
val sampleUiModule = module {
    viewModel<SampleViewModel>()

    navigation<SampleRoute> {
        SampleScreen()
    }
}
