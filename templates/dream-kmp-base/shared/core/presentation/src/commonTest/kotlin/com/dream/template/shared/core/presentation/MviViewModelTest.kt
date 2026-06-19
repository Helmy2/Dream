package com.dream.template.shared.core.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals

class MviViewModelTest {
    @Test
    fun processesActionsSequentially() = runBlocking {
        val actionScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        val viewModel = RecordingViewModel(actionScope = actionScope)

        try {
            viewModel.onAction(1)
            viewModel.onAction(2)
            withTimeout(2_000) {
                while (viewModel.state.value != 2) {
                    delay(10)
                }
            }

            assertEquals(listOf(1, 2), viewModel.processedActions)
            assertEquals(2, viewModel.state.value)
        } finally {
            actionScope.cancel()
        }
    }

    private class RecordingViewModel(
        actionScope: CoroutineScope
    ) : MviViewModel<Int, Int, Nothing>(
        initialState = 0,
        actionScope = actionScope
    ) {
        val processedActions = mutableListOf<Int>()

        override suspend fun handleAction(action: Int) {
            processedActions += action
            if (action == 1) {
                delay(50)
            }
            updateState { action }
        }
    }
}