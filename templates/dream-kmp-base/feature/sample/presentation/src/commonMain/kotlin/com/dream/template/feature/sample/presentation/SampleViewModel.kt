package com.dream.template.feature.sample.presentation

import com.dream.template.feature.sample.domain.repository.SampleRepository
import com.dream.template.shared.core.presentation.MviViewModel

class SampleViewModel(
    private val repository: SampleRepository
) : MviViewModel<SampleState, SampleAction, SampleEvent>(SampleState()) {

    init {
        load()
    }

    fun load() {
        updateState { current ->
            current.copy(items = repository.loadItems())
        }
    }

    override suspend fun handleAction(action: SampleAction) {
        when (action) {
            SampleAction.Load -> load()
        }
    }
}
