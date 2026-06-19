package com.dream.template.feature.sample.presentation

import com.dream.template.feature.sample.domain.model.SampleItem
import com.dream.template.feature.sample.domain.repository.SampleRepository
import com.dream.template.shared.core.domain.AppError
import com.dream.template.shared.core.domain.Result
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class SampleViewModelTest {
    @Test
    fun startsInLoadingStateWhileLoadIsRunning() = runTest {
        val releaseLoad = CompletableDeferred<Unit>()
        val viewModel = SampleViewModel(
            repository = object : SampleRepository {
                override suspend fun loadItems(): Result<List<SampleItem>> {
                    releaseLoad.await()
                    return Result.Success(emptyList())
                }
            },
            actionScope = backgroundScope
        )

        backgroundScope.launch {
            viewModel.load(force = true)
        }
        runCurrent()

        assertIs<SampleContentState.Loading>(viewModel.state.value.contentState)

        releaseLoad.complete(Unit)
        advanceUntilIdle()
    }

    @Test
    fun loadsStarterCards() = runTest {
        val viewModel = SampleViewModel(
            repository = object : SampleRepository {
                override suspend fun loadItems(): Result<List<SampleItem>> = Result.Success(
                    listOf(
                        SampleItem("One", "A"),
                        SampleItem("Two", "B"),
                        SampleItem("Three", "C")
                    )
                )
            }
        )

        viewModel.load(force = true)

        val contentState = assertIs<SampleContentState.Content>(viewModel.state.value.contentState)
        assertEquals(3, contentState.items.size)
    }

    @Test
    fun exposesEmptyState() = runTest {
        val viewModel = SampleViewModel(
            repository = object : SampleRepository {
                override suspend fun loadItems(): Result<List<SampleItem>> = Result.Success(emptyList())
            }
        )

        viewModel.load(force = true)

        assertIs<SampleContentState.Empty>(viewModel.state.value.contentState)
    }

    @Test
    fun exposesErrorState() = runTest {
        val viewModel = SampleViewModel(
            repository = object : SampleRepository {
                override suspend fun loadItems(): Result<List<SampleItem>> = Result.Failure(AppError.Message("No data source"))
            }
        )

        viewModel.load(force = true)

        val contentState = assertIs<SampleContentState.Error>(viewModel.state.value.contentState)
        assertEquals("No data source", contentState.message)
    }

}
