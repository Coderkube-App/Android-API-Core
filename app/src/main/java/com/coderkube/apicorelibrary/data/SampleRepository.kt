package com.coderkube.apicorelibrary.data

import com.example.apicore.executor.ApiExecutor
import com.example.apicore.init.ApiCore
import javax.inject.Inject

class SampleRepository @Inject constructor(
    private val apiExecutor: ApiExecutor
) {
    private val userApi: UserApi by lazy {
        ApiCore.createService<UserApi>()
    }

    suspend fun getUsers() = apiExecutor.execute {
        userApi.getUsers()
    }
    
    // Also demonstrating retry mechanism
    suspend fun getUsersWithRetry() = ApiCore.executeWithRetry(retryCount = 3) {
        userApi.getUsers()
    }
}
