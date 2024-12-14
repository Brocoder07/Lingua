package com.deploy.language_app

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deploy.language_app.api.BackendApi
import com.deploy.language_app.api.RetrofitClient
import com.deploy.language_app.api.Chat
import com.deploy.language_app.api.Message
import kotlinx.coroutines.launch

//ViewModel to manage chats
class ChatViewModel : ViewModel() {
    private val apiInstance: BackendApi = RetrofitClient.instance

    fun getChatData(userId: String) {
        viewModelScope.launch {
            try {
                val chatList = apiInstance.getChatsByUser(userId)
                if (chatList.chats.isNotEmpty()) {
                    chatHistory.clear()
                    chatHistory.addAll(chatList.chats)
                    Log.d("ChatViewModel", "Chat Id: ${chatList.chats[0]._id}")
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Unexpected: ${e.message}")
            }
        }
    }

    fun getMessageData(chatId: String, userId: String) {
        viewModelScope.launch {
            try {
                messageHistory.clear()
                val messageList = apiInstance.getChatsByUser(userId)
                Log.d("ChatViewModel", "chatid: $chatId")
                for (chat in messageList.chats) {
                    for (message in chat.messages) {
                        if (chat._id == chatId) {
                            messageHistory.addAll(chat.messages)
                            break
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    fun createChat(userId: String, language: String, level: String, title: String) {
        viewModelScope.launch {
            try {
                val response = apiInstance.createChat(userId, language, title, level)
                if (response != null) {
                    getChatData(userId)
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Unexpected: ${e.message}")
            }
        }
    }

    fun sendMessage(messageIn: Message, chatId: String, userId: String) {
        viewModelScope.launch {
            try {
                val response = apiInstance.sendMessage(chatId, messageIn)
                val message = Message(response.response, "bot", response.progress.last_interaction, response.progress.language)
                if (response != null) {
                    messageHistory.add(message)
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Unexpected: ${e.message}")
            }
        }
    }


    var chatHistory = mutableStateListOf<Chat>()
        private set

    fun addChat(chat: Chat){
        chatHistory.add(chat)
    }
    fun deleteChat(chat: Chat) {
        chatHistory.remove(chat)
    }
    fun getChat(chatId: String): Chat? = chatHistory.find { it._id == chatId }

    var messageHistory = mutableStateListOf<Message>()
        private set

    fun addMessage(message: Message){
        messageHistory.add(message)
    }
}
