package com.dream.template.feature.sample.impl

import com.dream.template.feature.sample.domain.model.SampleItem
import com.dream.template.shared.core.domain.TimeProvider

class SamplePresentationMapper(
    private val timeProvider: TimeProvider,
) {
    fun toFormState(item: SampleItem): SampleFormState = SampleFormState(
        id = item.id,
        title = SampleTextUi.Raw(item.title),
        description = SampleTextUi.Raw(item.description),
        category = item.category,
    )

    fun toItemUi(item: SampleItem): SampleItemUi = SampleItemUi(
        id = item.id,
        title = SampleTextUi.Raw(item.title),
        description = SampleTextUi.Raw(item.description),
        category = item.category,
        occurredLabel = timeProvider.formatDate(item.occurredAtMillis),
    )

    fun toPersistedValue(text: SampleTextUi): String = when (text) {
        is SampleTextUi.Raw -> text.value.trim()
    }
}
