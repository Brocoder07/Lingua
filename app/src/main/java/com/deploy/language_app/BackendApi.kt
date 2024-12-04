package com.deploy.language_app.api

import com.deploy.language_app.UserProfile
import retrofit2.http.*

data class User(val firebase_uid: String, val email: String)

interface BackendApi {
    @POST("/users")
    suspend fun registerUser(@Body user: User): Map<String, String>
    @GET("/users/{firebase_uid}")
    suspend fun getUserProfile(@Path("firebase_uid") firebaseUid: String): UserProfile
    @PUT("/users/{firebase_uid}")
    suspend fun updateUserProfile(
        @Path("firebase_uid") firebaseUid: String,
        @Body userUpdate: UserProfile
    ): UserProfile
}