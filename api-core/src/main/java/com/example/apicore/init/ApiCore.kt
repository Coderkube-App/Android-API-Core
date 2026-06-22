package com.example.apicore.init

import android.content.Context
import com.example.apicore.executor.ApiExecutor
import com.example.apicore.network.NetworkConfig
import com.example.apicore.network.RetrofitProvider
import com.example.apicore.result.ApiResult
import com.example.apicore.utils.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

object ApiCore {

    private val retrofitProvider = RetrofitProvider()
    private val apiExecutor = ApiExecutor()
    private var networkMonitor: NetworkMonitor? = null

    fun initialize(
        context: Context,
        baseUrl: String,
        enableLogging: Boolean = false,
        connectTimeout: Long = 30L,
        readTimeout: Long = 30L
    ) {
        val config = NetworkConfig(
            baseUrl = baseUrl,
            enableLogging = enableLogging,
            connectTimeout = connectTimeout,
            readTimeout = readTimeout,
            writeTimeout = readTimeout
        )
        retrofitProvider.initialize(config)
        networkMonitor = NetworkMonitor(context.applicationContext)
    }

    fun updateBaseUrl(newUrl: String) {
        retrofitProvider.updateBaseUrl(newUrl)
    }

    fun enableLogging(enable: Boolean) {
        retrofitProvider.enableLogging(enable)
    }

    inline fun <reified T> createService(): T {
        return createService(T::class.java)
    }

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofitProvider.createService(serviceClass)
    }

    fun addHeader(name: String, value: String) {
        retrofitProvider.headerInterceptor.addHeader(name, value)
    }

    fun removeHeader(name: String) {
        retrofitProvider.headerInterceptor.removeHeader(name)
    }

    fun setHeaderProvider(provider: () -> Map<String, String>) {
        retrofitProvider.headerInterceptor.dynamicHeaderProvider = provider
    }

    fun setAuthTokenProvider(provider: () -> String?) {
        retrofitProvider.authInterceptor.tokenProvider = provider
    }

    fun isNetworkAvailable(): Boolean {
        return networkMonitor?.isNetworkAvailable() ?: false
    }

    fun getNetworkStatusFlow(): Flow<Boolean> {
        return networkMonitor?.networkStatusFlow
            ?: throw IllegalStateException("ApiCore is not initialized")
    }

    suspend fun <T> executeWithRetry(
        retryCount: Int = 3,
        initialDelay: Long = 1000L,
        apiCall: suspend () -> Response<T>
    ): ApiResult<T> {
        return apiExecutor.executeWithRetry(retryCount, initialDelay, apiCall)
    }

    internal fun getRetrofitProvider(): RetrofitProvider = retrofitProvider
    internal fun getApiExecutor(): ApiExecutor = apiExecutor
}
