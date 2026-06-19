package com.dream.template.feature.sample.presentation

sealed interface SampleAction {
    data class Load(val force: Boolean = false) : SampleAction
}
