package com.deploy.language_app.api

import retrofit2.http.*

data class User(
    val firebase_uid: String,
    val email: String,
    val languages : List<String>,
    val created_at: String,
    val is_active: Boolean
)

data class UserGet(
    val _id: String,
    val firebase_uid: String,
    val email: String,
    val languages : List<String>,
    val created_at: String,
    val is_active: Boolean
)

data class UserUpdate(
    val firebase_uid: String?,
    val email: String?,
    val languages : List<String>?,
    val created_at: String?,
    val is_active: Boolean?
)

data class Message(
    val content: String,
    val role: String,
    val timestamp: String,
    val language: String,
)

data class Chat(
    val _id: String?,
    val user_id: String,
    val language: String,
    val level: String,
    val title: String,
    val messages: List<Message>,
    val is_active: Boolean
)

data class ChatPost(
    val user_id: String,
    val language: String,
    val level: String,
    val title: String,
    val messages: List<Message>,
    val is_active: Boolean
)

data class ChatList(
    val chats: List<Chat>
)

data class Question(
    val question: String,
    val options: List<String>,
    val correct_answer: String,
    val explanation: String,
    val topic: String,
    val difficulty: String
)

data class Test(
    val user_id: String,
    val chat_id: String,
    val questions: List<Question>,
    val language: String,
    val level: String,
    val results: Map<String, String>?
)

data class Response(
    val response: String,
    val progress: Progress
)

data class Progress(
    val user_id: String,
    val language: String,
    val last_interaction: String,
    val level: String,
    val messages_sent: Int,
    val xp_points: Int
)

interface BackendApi {
    @POST("/users")
    suspend fun registerUser(@Body user: User): Map<String, String>

    @GET("/users/{firebase_uid}")
    suspend fun getUserProfile(@Path("firebase_uid") firebase_uid: String): UserGet

    @PUT("/users/{firebase_uid}")
    suspend fun updateUserProfile(
        @Path("firebase_uid") firebaseUid: String,
        @Body userUpdate: UserUpdate
    ): User

    @POST("/chats/create")
    suspend fun createChat(@Query("user_id") user_id: String, @Query("language") language: String, @Query("initial_message") title: String, @Query("level") level: String): Chat

    @POST("/chats/{chat_id}/message")
    suspend fun sendMessage(@Path("chat_id") chatId: String, @Body updateData: Message): Response

    @GET("/chats/user/{user_id}")
    suspend fun getChatsByUser(@Path("user_id") userId: String): ChatList

    @POST("/chats/{chat_id}/generate-test")
    suspend fun generateTest(@Path("chat_id") chatId: String): Map<String, String>

    @GET("/tests/{test_id}/submit")
    suspend fun submitTest(@Path("test_id") testId: String): Map<String, String>

    @GET("/users/user_id/tests")
    suspend fun getTestsByUser(@Path("user_id") userId: String): Map<String, String>

}