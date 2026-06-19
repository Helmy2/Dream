package com.dream.template.feature.sample.presentation

import com.dream.template.feature.sample.domain.model.SampleItem

data class SampleState(
    val contentState: SampleContentState = SampleContentState.Loading
)

sealed interface SampleContentState {
    data object Loading : SampleContentState
    data object Empty : SampleContentState
    data class Content(val items: List<SampleItem>) : SampleContentState
    data class Error(val message: String) : SampleContentState
}
