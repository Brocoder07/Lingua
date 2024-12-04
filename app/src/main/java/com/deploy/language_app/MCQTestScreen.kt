package com.deploy.language_app

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MCQTestScreen(
    navController: NavHostController,
    questions: List<String>,
    answers: List<List<String>>,
    onSubmit: (IntArray) -> Unit
) {
    //Ensuring the size of selected answers matches the number of questions
    val selectedAnswers = remember { mutableStateOf(IntArray(questions.size) { -1 }) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MCQ Test") },
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
                    .background(brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF3366FF), Color(0xFF00CCFF))
                    ))
            ) {
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
                                    text = questions[index],
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(bottom = 8.dp),
                                    color = Color.White
                                )
                                answers.getOrNull(index)?.forEachIndexed { optionIndex, option ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        RadioButton(
                                            selected = selectedAnswers.value[index] == optionIndex,
                                            onClick = {
                                                selectedAnswers.value = selectedAnswers.value.apply {
                                                    this[index] = optionIndex
                                                }
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
                        onClick = { onSubmit(selectedAnswers.value) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    )
}