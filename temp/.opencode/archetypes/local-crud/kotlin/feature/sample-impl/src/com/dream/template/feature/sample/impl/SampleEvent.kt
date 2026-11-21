package com.dream.template.feature.sample.impl

sealed interface SampleEvent {
    data class NavigateToDetail(val id: String) : SampleEvent
    data object NavigateBack : SampleEvent
    data object SaveSucceeded : SampleEvent
}
