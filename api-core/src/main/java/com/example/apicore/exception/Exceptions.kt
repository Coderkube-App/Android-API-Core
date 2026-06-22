package com.example.apicore.exception

open class ApiException(
    val code: Int?,
    override val message: String,
    cause: Throwable? = null
) : Exception(message, cause)

class NetworkException(
    message: String,
    cause: Throwable? = null
) : ApiException(null, message, cause)
