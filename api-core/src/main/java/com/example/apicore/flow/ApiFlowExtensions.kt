package com.example.apicore.flow

import com.example.apicore.executor.safeApiCall
import com.example.apicore.result.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

fun <T> executeAsFlow(
    apiCall: suspend () -> Response<T>
): Flow<ApiResult<T>> = flow {
    emit(ApiResult.Loading)
    val result = safeApiCall(apiCall)
    emit(result)
}
