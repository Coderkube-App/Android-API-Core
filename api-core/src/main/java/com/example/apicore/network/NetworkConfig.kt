package com.example.apicore.network

data class NetworkConfig(
    var baseUrl: String,
    var enableLogging: Boolean = false,
    var connectTimeout: Long = 30L,
    var readTimeout: Long = 30L,
    var writeTimeout: Long = 30L
)
