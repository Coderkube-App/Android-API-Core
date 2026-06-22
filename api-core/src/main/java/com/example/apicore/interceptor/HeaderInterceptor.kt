package com.example.apicore.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.ConcurrentHashMap

class HeaderInterceptor : Interceptor {
    private val staticHeaders = ConcurrentHashMap<String, String>()
    var dynamicHeaderProvider: (() -> Map<String, String>)? = null

    fun addHeader(name: String, value: String) {
        staticHeaders[name] = value
    }

    fun removeHeader(name: String) {
        staticHeaders.remove(name)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Add static headers
        staticHeaders.forEach { (name, value) ->
            requestBuilder.addHeader(name, value)
        }

        // Add dynamic headers
        dynamicHeaderProvider?.invoke()?.forEach { (name, value) ->
            requestBuilder.addHeader(name, value)
        }

        return chain.proceed(requestBuilder.build())
    }
}
