package com.deploy.language_app

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class Chat(
    val id: String,
    val language: String = "",
    val level: String = "",
    val messages: MutableList<String> = mutableListOf()
)
//ViewModel to manage chats
class ChatViewModel : ViewModel() {
    var chatHistory = mutableStateListOf<Chat>()
        private set
    fun addChat(chat: Chat){
        chatHistory.add(chat)
    }
    fun deleteChat(chat: Chat) {
        chatHistory.remove(chat)
    }
    fun getChat(chatId: String): Chat? = chatHistory.find { it.id == chatId }
}
