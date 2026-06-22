package com.example.apicore.state

import com.example.apicore.result.ApiResult

sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

fun <T> ApiResult<T>.toUiState(): UiState<T> {
    return when (this) {
        is ApiResult.Loading -> UiState.Loading
        is ApiResult.Success -> UiState.Success(data)
        is ApiResult.Error -> UiState.Error(error.message)
    }
}
