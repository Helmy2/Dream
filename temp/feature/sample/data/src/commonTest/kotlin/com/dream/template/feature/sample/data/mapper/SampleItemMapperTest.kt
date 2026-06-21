package com.dream.template.feature.sample.data.mapper

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
            position = 1,
        )

        val item = entity.toDomain()

        assertEquals("Shared architecture", item.title)
        assertEquals("Keep domain, data, and presentation split.", item.description)
    }
}
