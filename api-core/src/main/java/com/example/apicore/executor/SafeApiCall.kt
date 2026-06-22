package com.example.apicore.executor

import com.example.apicore.exception.ApiException
import com.example.apicore.exception.NetworkException
import com.example.apicore.result.ApiError
import com.example.apicore.result.ApiResult
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CancellationException
import retrofit2.Response
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<T>
): ApiResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                ApiResult.Success(body)
            } else {
                // Determine if empty body is acceptable. Some APIs return 204 No Content
                if (response.code() == 204 || response.code() == 205) {
                    @Suppress("UNCHECKED_CAST")
                    ApiResult.Success(Unit as T)
                } else {
                    ApiResult.Error(ApiError(response.code(), "Response body is null"))
                }
            }
        } else {
            val errorBody = response.errorBody()?.string() ?: "Unknown HTTP error"
            ApiResult.Error(ApiError(response.code(), errorBody))
        }
    } catch (e: Exception) {
        val apiError = when (e) {
            is UnknownHostException,
            is SocketException,
            is SocketTimeoutException,
            is SSLException,
            is IOException -> {
                ApiError(null, "Network connection error: ${e.message}", e)
            }
            is JsonSyntaxException -> {
                ApiError(null, "Data parsing error", e)
            }
            is CancellationException -> {
                throw e // Propagate coroutine cancellation
            }
            else -> {
                ApiError(null, e.message ?: "An unexpected error occurred", e)
            }
        }
        ApiResult.Error(apiError)
    }
}
