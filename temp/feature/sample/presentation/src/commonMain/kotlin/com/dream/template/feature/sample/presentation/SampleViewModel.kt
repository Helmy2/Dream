package com.dream.template.feature.sample.presentation

import com.dream.template.feature.sample.domain.repository.SampleRepository
import com.dream.template.shared.core.domain.AppError
import com.dream.template.shared.core.domain.Result
import com.dream.template.shared.core.presentation.MviViewModel
import org.koin.core.annotation.KoinViewModel

class SampleViewModel(
    private val repository: SampleRepository,
) : MviViewModel<SampleState, SampleAction, SampleEvent>(
    initialState = SampleState(),
) {
    private var hasLoadedOnce = false

    suspend fun load(force: Boolean = false) {
        if (hasLoadedOnce && !force) return

        hasLoadedOnce = true
        updateState { current ->
            current.copy(contentState = SampleContentState.Loading)
        }

        when (val result = repository.loadItems()) {
            is Result.Success -> updateState { current ->
                current.copy(
                    contentState = if (result.value.isEmpty()) {
                        SampleContentState.Empty
                    } else {
                        SampleContentState.Content(result.value)
                    }
                )
            }

            is Result.Failure -> updateState { current ->
                current.copy(contentState = SampleContentState.Error(result.error.toMessage()))
            }
        }
    }

    override suspend fun handleAction(action: SampleAction) {
        when (action) {
            is SampleAction.Load -> load(force = action.force)
        }
    }

    private fun AppError.toMessage(): String = when (this) {
        AppError.Unknown -> "Something went wrong while loading the starter content."
        is AppError.Message -> value
    }
}
