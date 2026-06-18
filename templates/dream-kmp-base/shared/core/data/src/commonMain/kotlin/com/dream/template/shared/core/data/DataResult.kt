package com.dream.template.shared.core.data

import com.dream.template.shared.core.domain.AppError

sealed interface DataResult<out T> {
    data class Success<T>(val value: T) : DataResult<T>
    data class Failure(val error: AppError) : DataResult<Nothing>
}
