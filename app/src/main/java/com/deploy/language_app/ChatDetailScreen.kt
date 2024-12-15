package com.deploy.language_app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.deploy.language_app.api.Message
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chatId: String,
    chatViewModel: ChatViewModel,
    authViewModel: AuthViewModel,
    navController: NavHostController
) {
    val chat = chatViewModel.getChat(chatId)
    var messageInput by remember { mutableStateOf("") }
    val messageHistory: SnapshotStateList<Message> = chatViewModel.messageHistory

    LaunchedEffect(messageHistory) {
        authViewModel.userData.value?.let { chatViewModel.getMessageData(chatId, it.user_id) }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat ${chat?.title ?: "Unknown"}") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                // Chat messages list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(8.dp),
                ) {
                    items(messageHistory) { message ->
                        MessageItem(message)
                    }
                }
                //Floating Action Button for Test
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp), //Reduce bottom padding
                    contentAlignment = Alignment.BottomEnd
                ) {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate("mcq_test/$chatId")
                        },
                        modifier = Modifier.padding(end = 2.dp, bottom = 8.dp) //Minimal bottom padding
                    ) {
                        Text("Test")
                    }
                }
                //Message input and send button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.LightGray, MaterialTheme.shapes.small)
                                .padding(7.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            if (messageInput.isNotEmpty()) {
                                val message = Message(messageInput, "user", Instant.now().toString(), "English")
                                messageHistory.add(message)
                                authViewModel.userData.value?.let { chatViewModel.sendMessage(message, chatId, it.user_id) }
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
}
@Composable
fun MessageItem(message: Message) {
    val isUserMessage = message.role == "user"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start){
        Box(
            modifier = Modifier
                .background(
                    color = if (isUserMessage) Color.Green else Color.Gray,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(8.dp)
        ) {
            Text(text = message.content, color = Color.White)
        }
    }
}
