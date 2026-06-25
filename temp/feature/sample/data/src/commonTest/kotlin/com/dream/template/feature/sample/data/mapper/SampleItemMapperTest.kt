package com.dream.template.feature.sample.data.mapper

import com.dream.template.feature.sample.domain.model.SampleCategory
import com.dream.template.feature.sample.domain.model.SampleItem
import com.dream.template.shared.core.data.local.SampleItemEntity
import kotlin.test.Test
import kotlin.test.assertEquals

class SampleItemMapperTest {
    @Test
    fun mapsEntityToDomainItem() {
        val entity = SampleItemEntity(
            id = "shared-architecture",
            title = "Shared architecture",
            description = "Keep domain, data, and presentation split.",
            category = SampleCategory.Architecture.name,
            occurredAtMillis = 100L,
            updatedAtMillis = 200L,
            position = 1,
        )

        val item = entity.toDomain()

        assertEquals("shared-architecture", item.id)
        assertEquals("Shared architecture", item.title)
        assertEquals("Keep domain, data, and presentation split.", item.description)
        assertEquals(SampleCategory.Architecture, item.category)
        assertEquals(100L, item.occurredAtMillis)
        assertEquals(200L, item.updatedAtMillis)
    }

    @Test
    fun mapsInvalidCategoryToContractFallback() {
        val entity = SampleItemEntity(
            id = "legacy-item",
            title = "Legacy item",
            description = "Unknown category should not break reads.",
            category = "Legacy",
            occurredAtMillis = 100L,
            updatedAtMillis = 200L,
            position = 0,
        )

        val item = entity.toDomain()

        assertEquals(SampleCategory.Contract, item.category)
    }

    @Test
    fun mapsDomainItemToEntity() {
        val item = SampleItem(
            id = "desktop-preview",
            title = "Desktop preview loop",
            description = "Use desktop for fast previews.",
            category = SampleCategory.Preview,
            occurredAtMillis = 300L,
            updatedAtMillis = 400L,
        )

        val entity = item.toEntity(position = 2)

        assertEquals("desktop-preview", entity.id)
        assertEquals("Desktop preview loop", entity.title)
        assertEquals("Use desktop for fast previews.", entity.description)
        assertEquals("Preview", entity.category)
        assertEquals(300L, entity.occurredAtMillis)
        assertEquals(400L, entity.updatedAtMillis)
        assertEquals(2, entity.position)
    }
}
