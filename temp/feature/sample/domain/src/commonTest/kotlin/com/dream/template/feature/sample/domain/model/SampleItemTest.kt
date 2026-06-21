package com.dream.template.feature.sample.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class SampleItemTest {
    @Test
    fun preservesTitleAndDescription() {
        val item = SampleItem(
            title = "Contract-first features",
            description = "Generate features from compact contracts.",
        )

        assertEquals("Contract-first features", item.title)
        assertEquals("Generate features from compact contracts.", item.description)
    }
}
