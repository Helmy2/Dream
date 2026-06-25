package com.dream.template.feature.sample.impl

import com.dream.template.feature.sample.domain.model.SampleCategory
import com.dream.template.feature.sample.domain.model.SampleItem
import com.dream.template.feature.sample.domain.repository.SampleRepository
import com.dream.template.shared.core.domain.AppError
import com.dream.template.shared.core.domain.Result
import com.dream.template.shared.core.domain.TimeProvider
import com.dream.template.shared.core.presentation.MviViewModel

class SampleViewModel(
    private val repository: SampleRepository,
    private val timeProvider: TimeProvider,
    private val mapper: SamplePresentationMapper,
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
                        SampleContentState.Content(result.value.map { item -> mapper.toItemUi(item) })
                    }
                )
            }

            is Result.Failure -> updateState { current ->
                current.copy(contentState = SampleContentState.Error(result.error))
            }
        }
    }

    override suspend fun handleAction(action: SampleAction) {
        when (action) {
            is SampleAction.Load -> load(force = action.force)
            is SampleAction.SelectItem -> selectItem(action.id, navigate = action.navigate)
            SampleAction.StartCreate -> startCreate()
            SampleAction.StartEdit -> startEdit()
            SampleAction.CancelEdit -> cancelEdit()
            is SampleAction.TitleChanged -> updateState { state ->
                state.copy(formState = state.formState.copy(title = SampleTextUi.Raw(action.value), titleError = false))
            }

            is SampleAction.DescriptionChanged -> updateState { state ->
                state.copy(formState = state.formState.copy(description = SampleTextUi.Raw(action.value), descriptionError = false))
            }

            is SampleAction.CategoryChanged -> updateState { state ->
                state.copy(formState = state.formState.copy(category = action.value))
            }

            SampleAction.Save -> save()
        }
    }

    private suspend fun selectItem(id: String, navigate: Boolean) {
        when (val result = repository.loadItem(id)) {
            is Result.Success -> {
                val item = result.value ?: return
                updateState { current ->
                    current.copy(
                        detailState = SampleDetailState(item = mapper.toItemUi(item)),
                        formState = mapper.toFormState(item),
                    )
                }
                if (navigate) {
                    sendEvent(SampleEvent.NavigateToDetail(id))
                }
            }

            is Result.Failure -> updateState { current ->
                current.copy(contentState = SampleContentState.Error(result.error))
            }
        }
    }

    private fun startCreate() {
        updateState { current ->
            current.copy(
                detailState = SampleDetailState(
                    item = SampleItem(
                        id = "new",
                        title = "",
                        description = "",
                        category = SampleCategory.Contract,
                        occurredAtMillis = timeProvider.nowMillis(),
                        updatedAtMillis = timeProvider.nowMillis(),
                    ).let(mapper::toItemUi),
                    isEditing = true,
                ),
                formState = SampleFormState(),
            )
        }
    }

    private fun startEdit() {
        updateState { current ->
            val detail = current.detailState ?: return@updateState current
            current.copy(detailState = detail.copy(isEditing = true))
        }
    }

    private fun cancelEdit() {
        updateState { current ->
            val detail = current.detailState ?: return@updateState current
            current.copy(
                detailState = detail.copy(isEditing = false),
                formState = current.formState.copy(titleError = false, descriptionError = false),
            )
        }
    }

    private suspend fun save() {
        val form = state.value.formState
        val title = mapper.toPersistedValue(form.title)
        val description = mapper.toPersistedValue(form.description)
        val titleError = title.isBlank()
        val descriptionError = description.isBlank()
        if (titleError || descriptionError) {
            updateState { current ->
                current.copy(formState = current.formState.copy(titleError = titleError, descriptionError = descriptionError))
            }
            return
        }

        updateState { current -> current.copy(isSaving = true) }
        val result = if (form.id == null) {
            repository.createItem(title, description, form.category)
        } else {
            val currentItem = repository.loadItem(form.id)
            when (currentItem) {
                is Result.Success -> {
                    val item = currentItem.value ?: return updateSaveFailure(AppError.Unknown)
                    repository.updateItem(
                        item.update(
                            title = title,
                            description = description,
                            category = form.category,
                            updatedAtMillis = timeProvider.nowMillis(),
                        )
                    )
                }

                is Result.Failure -> return updateSaveFailure(currentItem.error)
            }
        }

        when (result) {
            is Result.Success -> {
                val ui = mapper.toItemUi(result.value)
                updateState { current ->
                    current.copy(
                        isSaving = false,
                        detailState = SampleDetailState(item = ui, isEditing = false),
                        formState = mapper.toFormState(result.value),
                    )
                }
                load(force = true)
                sendEvent(SampleEvent.SaveSucceeded)
            }

            is Result.Failure -> updateSaveFailure(result.error)
        }
    }

    private fun updateSaveFailure(error: AppError) {
        updateState { current ->
            current.copy(
                isSaving = false,
                contentState = SampleContentState.Error(error),
            )
        }
    }

}
