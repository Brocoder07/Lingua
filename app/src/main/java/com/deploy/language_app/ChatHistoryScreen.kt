package com.deploy.language_app

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatHistoryScreen(navController: NavHostController, chatViewModel: ChatViewModel, authViewModel: AuthViewModel) {
    val chatHistory = chatViewModel.chatHistory
    LaunchedEffect(chatHistory) {
        authViewModel.userData.value?.user_id?.let { chatViewModel.getChatData(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Chat History") })
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 80.dp, end = 16.dp)
            ) {
                //Floating Action Button for New Chat
                FloatingActionButton(onClick = {
                    navController.navigate("language_selection")
                }) {
                    Icon(Icons.Default.Add, contentDescription = "New Chat")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            LazyColumn(modifier = Modifier.fillMaxSize().background(brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF3366FF), Color(0xFF00CCFF))
            ))) {
                items(chatHistory) { chat ->
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("chat_detail/${chat._id}")
                            }
                            .combinedClickable(
                                onClick = {
                                    navController.navigate("chat_detail/${chat._id}")
                                },
                                onLongClick = {
                                    //Delete chat on long press
                                    chatViewModel.deleteChat(chat)
                                }
                            )
                            .padding(8.dp),
                        headlineContent = { Text(chat.title) },
                    )
                }
            }
        }
    }
}
