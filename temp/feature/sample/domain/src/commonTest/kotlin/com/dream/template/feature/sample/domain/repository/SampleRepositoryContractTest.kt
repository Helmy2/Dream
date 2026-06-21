package com.dream.template.feature.sample.domain.repository

import com.dream.template.feature.sample.domain.model.SampleItem
import com.dream.template.shared.core.domain.Result
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SampleRepositoryContractTest {
    @Test
    fun loadItemsReturnsDomainItemsWrappedInResult() = runTest {
        val repository = object : SampleRepository {
            override suspend fun loadItems(): Result<List<SampleItem>> = Result.Success(
                listOf(
                    SampleItem(
                        title = "Shared architecture",
                        description = "Keep feature logic in feature modules.",
                    )
                )
            )
        }

        val result = assertIs<Result.Success<List<SampleItem>>>(repository.loadItems())
        assertEquals("Shared architecture", result.value.single().title)
    }
}
