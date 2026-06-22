package com.coderkube.apicorelibrary.data

import retrofit2.Response
import retrofit2.http.GET

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String
)

interface UserApi {
    @GET("users")
    suspend fun getUsers(): Response<List<User>>
}
