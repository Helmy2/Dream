package com.dream.template.feature.sample.presentation

import app.cash.turbine.test
import com.dream.template.feature.sample.domain.model.SampleItem
import com.dream.template.feature.sample.domain.repository.SampleRepository
import com.dream.template.shared.core.domain.AppError
import com.dream.template.shared.core.domain.Result
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class SampleViewModelTest {
    private val mainDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(mainDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun startsInLoadingStateWhileLoadIsRunning() = runTest(mainDispatcher) {
        val releaseLoad = CompletableDeferred<Unit>()
        val repository = mock<SampleRepository> {
            everySuspend { loadItems() } calls {
                releaseLoad.await()
                Result.Success(emptyList())
            }
        }
        val viewModel = SampleViewModel(repository = repository)

        backgroundScope.launch {
            viewModel.load(force = true)
        }
        runCurrent()

        assertIs<SampleContentState.Loading>(viewModel.state.value.contentState)

        releaseLoad.complete(Unit)
        advanceUntilIdle()
    }

    @Test
    fun loadsStarterCards() = runTest(mainDispatcher) {
        val repository = mock<SampleRepository> {
            everySuspend { loadItems() } returns Result.Success(
                listOf(
                    SampleItem("One", "A"),
                    SampleItem("Two", "B"),
                    SampleItem("Three", "C")
                )
            )
        }
        val viewModel = SampleViewModel(repository = repository)

        viewModel.state.test {
            assertIs<SampleContentState.Loading>(awaitItem().contentState)

            viewModel.load(force = true)

            val contentState = assertIs<SampleContentState.Content>(awaitItem().contentState)
            assertEquals(3, contentState.items.size)
        }
    }

    @Test
    fun exposesEmptyState() = runTest(mainDispatcher) {
        val repository = mock<SampleRepository> {
            everySuspend { loadItems() } returns Result.Success(emptyList())
        }
        val viewModel = SampleViewModel(repository = repository)

        viewModel.state.test {
            assertIs<SampleContentState.Loading>(awaitItem().contentState)

            viewModel.load(force = true)

            assertIs<SampleContentState.Empty>(awaitItem().contentState)
        }
    }

    @Test
    fun exposesErrorState() = runTest(mainDispatcher) {
        val repository = mock<SampleRepository> {
            everySuspend { loadItems() } returns Result.Failure(AppError.Message("No data source"))
        }
        val viewModel = SampleViewModel(repository = repository)

        viewModel.state.test {
            assertIs<SampleContentState.Loading>(awaitItem().contentState)

            viewModel.load(force = true)

            val contentState = assertIs<SampleContentState.Error>(awaitItem().contentState)
            assertEquals("No data source", contentState.message)
        }
    }

}
