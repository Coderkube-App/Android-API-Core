package com.example.apicore.di

import com.example.apicore.executor.ApiExecutor
import com.example.apicore.init.ApiCore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiCoreModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return ApiCore.getRetrofitProvider().getRetrofit()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return ApiCore.getRetrofitProvider().getOkHttpClient()
    }

    @Provides
    @Singleton
    fun provideApiExecutor(): ApiExecutor {
        return ApiCore.getApiExecutor()
    }
}
