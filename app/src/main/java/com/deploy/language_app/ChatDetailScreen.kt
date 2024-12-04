package com.deploy.language_app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chatId: String,
    chatViewModel: ChatViewModel,
    navController: NavHostController
) {
    val chat = chatViewModel.getChat(chatId)
    var messageInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat ${chat?.id ?: "Unknown"}") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        //Apply a gradient background similar to ChatHistoryScreen
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF3366FF), Color(0xFF00CCFF))
                    )
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    itemsIndexed(chat?.messages ?: listOf()) { _, message ->
                        Text(
                            text = message,
                            modifier = Modifier.padding(8.dp),
                            color = Color.White //Changed to White for better contrast
                        )
                    }
                }
                Row(modifier = Modifier.padding(8.dp)) {
                    BasicTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        modifier = Modifier
                            .weight(2f)
                            .background(Color.LightGray, MaterialTheme.shapes.small)
                            .padding(7.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Button(onClick = {
                        if (messageInput.isNotEmpty()) {
                            chat?.messages?.add(messageInput)
                            messageInput = ""
                        }
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "Send")
                    }
                }
            }
        }
    }
}