package com.dream.template.feature.sample.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SampleScreen(
    viewModel: SampleViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { _ ->
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.onAction(SampleAction.Load())
    }

    SampleContent(
        state = state,
        onAction = { action -> viewModel.onAction(action) },
        onNavigateBack = onNavigateBack
    )
}
