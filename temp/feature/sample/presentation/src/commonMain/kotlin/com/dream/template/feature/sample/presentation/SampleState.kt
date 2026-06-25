package com.dream.template.feature.sample.presentation

import com.dream.template.feature.sample.domain.model.SampleCategory
import com.dream.template.shared.core.domain.AppError

data class SampleState(
    val contentState: SampleContentState = SampleContentState.Loading,
    val detailState: SampleDetailState? = null,
    val formState: SampleFormState = SampleFormState(),
    val isSaving: Boolean = false,
)

sealed interface SampleContentState {
    data object Loading : SampleContentState
    data object Empty : SampleContentState
    data class Content(val items: List<SampleItemUi>) : SampleContentState
    data class Error(val error: AppError) : SampleContentState
}

data class SampleItemUi(
    val id: String,
    val title: SampleTextUi,
    val description: SampleTextUi,
    val category: SampleCategory,
    val occurredLabel: String,
)

sealed interface SampleTextUi {
    data class Raw(val value: String) : SampleTextUi
    data class Resource(val resource: SampleTextResource) : SampleTextUi
}

enum class SampleTextResource {
    SeedContractFirstTitle,
    SeedContractFirstDescription,
    SeedSharedArchitectureTitle,
    SeedSharedArchitectureDescription,
    SeedDesktopPreviewTitle,
    SeedDesktopPreviewDescription,
}

data class SampleDetailState(
    val item: SampleItemUi,
    val isEditing: Boolean = false,
)

data class SampleFormState(
    val id: String? = null,
    val title: SampleTextUi = SampleTextUi.Raw(""),
    val description: SampleTextUi = SampleTextUi.Raw(""),
    val category: SampleCategory = SampleCategory.Contract,
    val titleError: Boolean = false,
    val descriptionError: Boolean = false,
)
