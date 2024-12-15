package com.deploy.language_app

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.deploy.language_app.api.BackendApi
import com.deploy.language_app.api.Question
import com.deploy.language_app.api.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MCQTestScreen(
    navController: NavHostController,
    chatId: String,
    chatViewModel: ChatViewModel,
    authViewModel: AuthViewModel
) {
    val apiInstance: BackendApi = RetrofitClient.instance
    val coroutineScope = rememberCoroutineScope()
    var questions by remember { mutableStateOf<List<Question>>(emptyList()) }
    var selectedAnswers by remember { mutableStateOf(mutableMapOf<Int, String>()) }
    var isLoading by remember { mutableStateOf(true) }
    var testId by remember { mutableStateOf("") }

    //Generate test when screen loads
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = apiInstance.generateTest(chatId)
                testId = response["test_id"] ?: ""
                Log.d("MCQTestScreen", "Generated Test ID: $testId")
                isLoading = false
            } catch (e: Exception) {
                Log.e("MCQTestScreen", "Error generating test: ${e.message}")
                isLoading = false
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Language Proficiency Test") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF3366FF), Color(0xFF00CCFF))
                        )
                    )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                } else if (questions.isEmpty()) {
                    Text(
                        text = "Unable to generate test. Please try again.",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(questions.size) { index ->
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = questions[index].question,
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(bottom = 8.dp),
                                        color = Color.White
                                    )
                                    questions[index].options.forEachIndexed { optionIndex, option ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                        ) {
                                            RadioButton(
                                                selected = selectedAnswers[index] == option,
                                                onClick = {
                                                    selectedAnswers[index] = option
                                                },
                                                colors = RadioButtonDefaults.colors(selectedColor = Color.White)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = option, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    try {
                                        val response = apiInstance.submitTest(testId)
                                        val score = response["score"] ?: "0"

                                        navController.navigate("test_result/$score")
                                    } catch (e: Exception) {
                                        Log.e("MCQTestScreen", "Error submitting test: ${e.message}")
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            enabled = selectedAnswers.size == 5
                        ) {
                            Text("Submit")
                        }
                    }
                }
            }
        }
    )
}
