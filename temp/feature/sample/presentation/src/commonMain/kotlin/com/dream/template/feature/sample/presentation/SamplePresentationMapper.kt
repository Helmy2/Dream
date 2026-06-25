package com.dream.template.feature.sample.presentation

import com.dream.template.feature.sample.domain.model.SampleItem
import com.dream.template.shared.core.domain.TimeProvider

class SamplePresentationMapper(
    private val timeProvider: TimeProvider,
) {
    fun toFormState(item: SampleItem): SampleFormState = SampleFormState(
        id = item.id,
        title = item.title.toSampleTextUi(),
        description = item.description.toSampleTextUi(),
        category = item.category,
    )

    fun toItemUi(item: SampleItem): SampleItemUi = SampleItemUi(
        id = item.id,
        title = item.title.toSampleTextUi(),
        description = item.description.toSampleTextUi(),
        category = item.category,
        occurredLabel = timeProvider.formatDate(item.occurredAtMillis),
    )

    fun toPersistedValue(text: SampleTextUi): String = when (text) {
        is SampleTextUi.Raw -> text.value.trim()
        is SampleTextUi.Resource -> text.resource.persistedValue()
    }

    private fun String.toSampleTextUi(): SampleTextUi = when (this) {
        SeedContractFirstTitleKey -> SampleTextUi.Resource(SampleTextResource.SeedContractFirstTitle)
        SeedContractFirstDescriptionKey -> SampleTextUi.Resource(SampleTextResource.SeedContractFirstDescription)
        SeedSharedArchitectureTitleKey -> SampleTextUi.Resource(SampleTextResource.SeedSharedArchitectureTitle)
        SeedSharedArchitectureDescriptionKey -> SampleTextUi.Resource(SampleTextResource.SeedSharedArchitectureDescription)
        SeedDesktopPreviewTitleKey -> SampleTextUi.Resource(SampleTextResource.SeedDesktopPreviewTitle)
        SeedDesktopPreviewDescriptionKey -> SampleTextUi.Resource(SampleTextResource.SeedDesktopPreviewDescription)
        else -> SampleTextUi.Raw(this)
    }

    private fun SampleTextResource.persistedValue(): String = when (this) {
        SampleTextResource.SeedContractFirstTitle -> SeedContractFirstTitleKey
        SampleTextResource.SeedContractFirstDescription -> SeedContractFirstDescriptionKey
        SampleTextResource.SeedSharedArchitectureTitle -> SeedSharedArchitectureTitleKey
        SampleTextResource.SeedSharedArchitectureDescription -> SeedSharedArchitectureDescriptionKey
        SampleTextResource.SeedDesktopPreviewTitle -> SeedDesktopPreviewTitleKey
        SampleTextResource.SeedDesktopPreviewDescription -> SeedDesktopPreviewDescriptionKey
    }

    private companion object {
        const val SeedContractFirstTitleKey = "sample.seed.contract_first.title"
        const val SeedContractFirstDescriptionKey = "sample.seed.contract_first.description"
        const val SeedSharedArchitectureTitleKey = "sample.seed.shared_architecture.title"
        const val SeedSharedArchitectureDescriptionKey = "sample.seed.shared_architecture.description"
        const val SeedDesktopPreviewTitleKey = "sample.seed.desktop_preview.title"
        const val SeedDesktopPreviewDescriptionKey = "sample.seed.desktop_preview.description"
    }
}
