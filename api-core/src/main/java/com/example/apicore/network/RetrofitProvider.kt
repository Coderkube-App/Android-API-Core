package com.example.apicore.network

import com.example.apicore.interceptor.AuthInterceptor
import com.example.apicore.interceptor.BaseUrlInterceptor
import com.example.apicore.interceptor.HeaderInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal class RetrofitProvider {

    val authInterceptor = AuthInterceptor()
    val headerInterceptor = HeaderInterceptor()
    val baseUrlInterceptor = BaseUrlInterceptor()
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.NONE
    }

    private lateinit var retrofit: Retrofit
    private lateinit var okHttpClient: OkHttpClient

    fun initialize(config: NetworkConfig) {
        baseUrlInterceptor.hostUrl = config.baseUrl
        loggingInterceptor.level = if (config.enableLogging) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }

        okHttpClient = OkHttpClient.Builder()
            .connectTimeout(config.connectTimeout, TimeUnit.SECONDS)
            .readTimeout(config.readTimeout, TimeUnit.SECONDS)
            .writeTimeout(config.writeTimeout, TimeUnit.SECONDS)
            .addInterceptor(baseUrlInterceptor)
            .addInterceptor(headerInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        retrofit = Retrofit.Builder()
            // We use a dummy base url here, it will be overridden by BaseUrlInterceptor if needed
            // But we pass the initial one so Retrofit doesn't crash on init
            .baseUrl(config.baseUrl.ifEmpty { "http://localhost/" })
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun updateBaseUrl(newUrl: String) {
        baseUrlInterceptor.hostUrl = newUrl
    }

    fun enableLogging(enable: Boolean) {
        loggingInterceptor.level = if (enable) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    fun <T> createService(serviceClass: Class<T>): T {
        if (!::retrofit.isInitialized) {
            throw IllegalStateException("ApiCore is not initialized. Call ApiCore.initialize() first.")
        }
        return retrofit.create(serviceClass)
    }
    
    fun getOkHttpClient(): OkHttpClient {
        if (!::okHttpClient.isInitialized) {
            throw IllegalStateException("ApiCore is not initialized. Call ApiCore.initialize() first.")
        }
        return okHttpClient
    }

    fun getRetrofit(): Retrofit {
        if (!::retrofit.isInitialized) {
            throw IllegalStateException("ApiCore is not initialized. Call ApiCore.initialize() first.")
        }
        return retrofit
    }
}
