package com.dream.template.shared.app

import androidx.compose.runtime.Composable
import com.dream.template.feature.sample.data.repository.InMemorySampleRepository
import com.dream.template.feature.sample.presentation.SampleScreen
import com.dream.template.feature.sample.presentation.SampleViewModel
import com.dream.template.shared.navigation.AppShell

@Composable
fun DreamApp() {
    val viewModel = SampleViewModel(repository = InMemorySampleRepository())
    AppShell(
        sampleContent = {
            SampleScreen(viewModel = viewModel)
        }
    )
}
