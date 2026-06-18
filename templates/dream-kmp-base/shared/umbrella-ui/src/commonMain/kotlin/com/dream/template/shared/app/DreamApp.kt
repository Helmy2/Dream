package com.dream.template.shared.app

import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.dream.template.feature.sample.data.repository.InMemorySampleRepository
import com.dream.template.feature.sample.presentation.SampleScreen
import com.dream.template.feature.sample.presentation.SampleViewModel
import com.dream.template.shared.navigation.AppShell

@Composable
fun DreamApp() {
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { InMemorySampleRepository() }
    val viewModel = remember(repository, coroutineScope) {
        SampleViewModel(
            repository = repository,
            coroutineScope = coroutineScope
        )
    }

    AppShell(
        sampleContent = {
            SampleScreen(viewModel = viewModel)
        }
    )
}
