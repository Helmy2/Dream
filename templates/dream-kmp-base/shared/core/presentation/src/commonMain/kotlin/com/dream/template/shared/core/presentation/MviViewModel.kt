package com.dream.template.shared.core.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class MviViewModel<State : Any, Action : Any, Event : Any>(
    initialState: State,
    private val scope: CoroutineScope
) {
    private val _state = MutableStateFlow(initialState)
    private val _events = MutableSharedFlow<Event>()

    val state: StateFlow<State> = _state.asStateFlow()
    val eventFlow: SharedFlow<Event> = _events.asSharedFlow()

    fun onAction(action: Action) {
        scope.launch {
            handleAction(action)
        }
    }

    protected fun updateState(transform: (State) -> State) {
        _state.update(transform)
    }

    protected suspend fun sendEvent(event: Event) {
        _events.emit(event)
    }

    protected abstract suspend fun handleAction(action: Action)
}
