package com.dream.template.feature.sample.data.mapper

import com.dream.template.feature.sample.domain.model.SampleItem
import com.dream.template.shared.core.data.local.SampleItemEntity

fun SampleItemEntity.toDomain(): SampleItem = SampleItem(
    title = title,
    description = description,
)
