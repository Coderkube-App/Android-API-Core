package com.example.apicore.interceptor

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

class BaseUrlInterceptor : Interceptor {
    @Volatile
    var hostUrl: String? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        hostUrl?.let { baseUrlString ->
            val newBaseUrl = baseUrlString.toHttpUrlOrNull()
            if (newBaseUrl != null) {
                val newUrl = request.url.newBuilder()
                    .scheme(newBaseUrl.scheme)
                    .host(newBaseUrl.host)
                    .port(newBaseUrl.port)
                    .build()
                request = request.newBuilder()
                    .url(newUrl)
                    .build()
            }
        }
        return chain.proceed(request)
    }
}
