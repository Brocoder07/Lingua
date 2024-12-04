package com.deploy.language_app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavHostController) {
    var chatInput by remember { mutableStateOf("") }
    val chatSessions = remember { mutableStateListOf(mutableStateListOf<String>()) }
    val sessionTitles = remember { mutableStateListOf("New Chat") }//Stores session titles
    var selectedSessionIndex by remember { mutableStateOf(0) }
    var isHistoryVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chatbot") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { isHistoryVisible = !isHistoryVisible }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF3366FF), Color(0xFF00CCFF))
                        )
                    )
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    if (isHistoryVisible) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(8.dp)
                        ) {
                            itemsIndexed(sessionTitles) { index, title ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    TextButton(
                                        onClick = {
                                            selectedSessionIndex = index
                                            isHistoryVisible = false
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(title, color = Color(0xFF3366FF))
                                    }
                                    IconButton(
                                        onClick = {
                                            //Delete chat session
                                            chatSessions.removeAt(index)
                                            sessionTitles.removeAt(index)
                                            //Ensure at least one session remains
                                            if (chatSessions.isEmpty()) {
                                                chatSessions.add(mutableStateListOf())
                                                sessionTitles.add("New Chat")
                                            }
                                            selectedSessionIndex = 0 //Reset to first session
                                        }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Chat", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        itemsIndexed(chatSessions[selectedSessionIndex]) { index, message ->
                            Text(
                                text = message,
                                fontSize = 16.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = chatInput,
                            onValueChange = { chatInput = it },
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.LightGray, MaterialTheme.shapes.small)
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            if (chatInput.isNotEmpty()) {
                                //Ensure sessionTitles list can accommodate the current index
                                while (sessionTitles.size <= selectedSessionIndex) {
                                    sessionTitles.add("New Chat")
                                }
                                chatSessions[selectedSessionIndex].add(chatInput)
                                if (chatSessions[selectedSessionIndex].size == 1) {
                                    val firstMessage = chatInput.trim()
                                    sessionTitles[selectedSessionIndex] =
                                        if (firstMessage.length > 20) firstMessage.take(20) + "..."
                                        else firstMessage
                                }
                                chatInput = ""
                            }
                        }) {
                            Text("Send")
                        }
                    }
                }
                FloatingActionButton(
                    onClick = {
                        chatSessions.add(mutableStateListOf())
                        sessionTitles.add("New Chat")
                        selectedSessionIndex = chatSessions.size - 1
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 80.dp, end = 16.dp),
                    containerColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Chat", tint = Color(0xFF3366FF))
                }
            }
        }
    )
}