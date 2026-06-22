package com.example.apicore.result

data class ApiError(
    val code: Int?,
    val message: String,
    val throwable: Throwable? = null
)
