package com.dream.template.feature.sample.data.repository

import com.dream.template.feature.sample.domain.model.SampleItem
import com.dream.template.feature.sample.domain.repository.SampleRepository

class InMemorySampleRepository : SampleRepository {
    override fun loadItems(): List<SampleItem> = listOf(
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
            description = "Use the desktop target as the fastest preview and MCP hot reload surface."
        )
    )
}
