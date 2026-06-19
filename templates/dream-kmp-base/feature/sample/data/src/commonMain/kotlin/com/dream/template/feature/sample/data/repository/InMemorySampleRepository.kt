package com.dream.template.feature.sample.data.repository

import com.dream.template.feature.sample.domain.model.SampleItem
import com.dream.template.feature.sample.domain.repository.SampleRepository
import com.dream.template.shared.core.domain.Result

class InMemorySampleRepository : SampleRepository {
    override suspend fun loadItems(): Result<List<SampleItem>> = Result.Success(
        listOf(
            SampleItem(
                title = "Contract-first features",
                description = "Generate features from compact contracts stored in .mvp/features/."
            ),
            SampleItem(
                title = "Shared architecture",
                description = "Keep domain, data, presentation, and UI code in stable modules."
            ),
            SampleItem(
                title = "Desktop preview loop",
                description = "Use the desktop target as the fastest preview and repair surface."
            )
        )
    )
}
