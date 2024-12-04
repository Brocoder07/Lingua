package com.deploy.language_app

data class UserProfile(
    val firebase_uid: String,
    val email: String,
    val languages: List<String>,
    val current_time: String // Use String instead of List<String> for the timestamp
)