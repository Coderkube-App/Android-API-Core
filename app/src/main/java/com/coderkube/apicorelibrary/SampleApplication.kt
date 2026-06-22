package com.coderkube.apicorelibrary

import android.app.Application
import com.example.apicore.init.ApiCore
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize API Core
        ApiCore.initialize(
            context = this,
            baseUrl = "https://jsonplaceholder.typicode.com/",
            enableLogging = true,
            connectTimeout = 30,
            readTimeout = 30
        )

        // Set optional auth token provider
        ApiCore.setAuthTokenProvider {
            "dummy_token"
        }
    }
}
