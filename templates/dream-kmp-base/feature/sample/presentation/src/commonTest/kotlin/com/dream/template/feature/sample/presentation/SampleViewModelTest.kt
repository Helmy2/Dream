package com.dream.template.feature.sample.presentation

import com.dream.template.feature.sample.domain.model.SampleItem
import com.dream.template.feature.sample.domain.repository.SampleRepository
import com.dream.template.shared.core.domain.AppError
import com.dream.template.shared.core.domain.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SampleViewModelTest {
    private val testScope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)

    @Test
    fun startsInLoadingState() {
        val viewModel = SampleViewModel(
            repository = object : SampleRepository {
                override fun loadItems(): Result<List<SampleItem>> = Result.Success(emptyList())
            },
            coroutineScope = testScope
        )

        assertIs<SampleContentState.Loading>(viewModel.state.value.contentState)
    }

    @Test
    fun loadsStarterCards() {
        val viewModel = SampleViewModel(
            repository = object : SampleRepository {
                override fun loadItems(): Result<List<SampleItem>> = Result.Success(
                    listOf(
                        SampleItem("One", "A"),
                        SampleItem("Two", "B"),
                        SampleItem("Three", "C")
                    )
                )
            },
            coroutineScope = testScope
        )

        viewModel.load()

        val contentState = assertIs<SampleContentState.Content>(viewModel.state.value.contentState)
        assertEquals(3, contentState.items.size)
    }

    @Test
    fun exposesEmptyState() {
        val viewModel = SampleViewModel(
            repository = object : SampleRepository {
                override fun loadItems(): Result<List<SampleItem>> = Result.Success(emptyList())
            },
            coroutineScope = testScope
        )

        viewModel.load()

        assertIs<SampleContentState.Empty>(viewModel.state.value.contentState)
    }

    @Test
    fun exposesErrorState() {
        val viewModel = SampleViewModel(
            repository = object : SampleRepository {
                override fun loadItems(): Result<List<SampleItem>> = Result.Failure(AppError.Message("No data source"))
            },
            coroutineScope = testScope
        )

        viewModel.load()

        val contentState = assertIs<SampleContentState.Error>(viewModel.state.value.contentState)
        assertEquals("No data source", contentState.message)
    }
}
