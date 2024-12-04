package com.deploy.language_app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

//ViewModel to manage chats
class ChatViewModel : ViewModel() {
    var chatHistory by mutableStateOf(listOf<Chat>())
        private set
    fun getChat(chatId: String): Chat? = chatHistory.find { it.id == chatId }
}
data class Chat(val id: String, val messages: MutableList<String> = mutableListOf())
@Composable
fun ChatDetailScreen(chatId: String, chatViewModel: ChatViewModel = viewModel()) {
    val chat = chatViewModel.getChat(chatId)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = chat?.id ?: "Chat Not Found",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}