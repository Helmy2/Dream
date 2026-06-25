package com.dream.template.feature.sample.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SampleScreen(
    viewModel: SampleViewModel = koinViewModel(),
    selectedId: String? = null,
    onNavigateDetail: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is SampleEvent.NavigateToDetail -> onNavigateDetail(event.id)
                SampleEvent.NavigateBack,
                SampleEvent.SaveSucceeded -> Unit
            }
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.onAction(SampleAction.Load())
    }

    LaunchedEffect(viewModel, selectedId) {
        if (selectedId != null) {
            viewModel.onAction(SampleAction.SelectItem(selectedId, navigate = false))
        }
    }

    SampleContent(
        state = state,
        onAction = { action -> viewModel.onAction(action) },
        onNavigateBack = onNavigateBack
    )
}
