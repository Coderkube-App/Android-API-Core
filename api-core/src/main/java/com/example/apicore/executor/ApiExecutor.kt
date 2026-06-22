package com.example.apicore.executor

import com.example.apicore.result.ApiError
import com.example.apicore.result.ApiResult
import kotlinx.coroutines.delay
import retrofit2.Response

class ApiExecutor {

    suspend fun <T> execute(
        apiCall: suspend () -> Response<T>
    ): ApiResult<T> {
        return safeApiCall(apiCall)
    }

    suspend fun <T> executeWithRetry(
        retryCount: Int = 3,
        initialDelay: Long = 1000L,
        apiCall: suspend () -> Response<T>
    ): ApiResult<T> {
        var currentDelay = initialDelay
        repeat(retryCount - 1) {
            val result = safeApiCall(apiCall)
            if (result is ApiResult.Success) {
                return result
            } else if (result is ApiResult.Error) {
                if (!shouldRetry(result.error)) {
                    return result
                }
            }
            delay(currentDelay)
            currentDelay *= 2 // Exponential backoff
        }
        // Last attempt
        return safeApiCall(apiCall)
    }

    private fun shouldRetry(error: ApiError): Boolean {
        // Do not retry on client/server errors like 400, 401, 403, 404, 500
        val code = error.code
        if (code != null) {
            if (code in 400..599) {
                return false
            }
        }
        // Retry on network errors (where code is null and throwable is IOException)
        return true
    }
}
