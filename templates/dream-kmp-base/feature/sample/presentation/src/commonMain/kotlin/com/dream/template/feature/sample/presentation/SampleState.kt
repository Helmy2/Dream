package com.dream.template.feature.sample.presentation

import com.dream.template.feature.sample.domain.model.SampleItem

data class SampleState(
    val title: String = "Dream MVP Builder",
    val subtitle: String = "Reusable starting point for generated mobile-first apps.",
    val items: List<SampleItem> = emptyList()
)
