package com.dream.template.feature.sample.presentation

import com.dream.template.feature.sample.domain.model.SampleItem
import com.dream.template.feature.sample.domain.repository.SampleRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class SampleViewModelTest {
    @Test
    fun loadsStarterCards() {
        val viewModel = SampleViewModel(
            repository = object : SampleRepository {
                override fun loadItems(): List<SampleItem> = listOf(
                    SampleItem("One", "A"),
                    SampleItem("Two", "B"),
                    SampleItem("Three", "C")
                )
            }
        )

        assertEquals(3, viewModel.state.value.items.size)
    }
}
