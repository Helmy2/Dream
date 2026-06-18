package com.dream.template.feature.sample.presentation

sealed interface SampleAction {
    data object Load : SampleAction
}
