package com.deploy.language_app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionScreen(
    navController: NavHostController,
    onSelectionComplete: (String, String) -> Unit
) {
    var selectedLanguage by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf("") }
    var isLanguageDropdownExpanded by remember { mutableStateOf(false) }
    var isLevelDropdownExpanded by remember { mutableStateOf(false) }
    val languages = listOf("English", "Spanish", "French", "German", "Mandarin", "Hindi", "Japanese")
    val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Language and Level") }
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
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //Dropdown for Language
                Box {
                    Text(
                        text = if (selectedLanguage.isEmpty()) "Select a Language" else selectedLanguage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black, MaterialTheme.shapes.small)
                            .padding(8.dp)
                            .clickable { isLanguageDropdownExpanded = true }
                    )
                    DropdownMenu(
                        expanded = isLanguageDropdownExpanded,
                        onDismissRequest = { isLanguageDropdownExpanded = false }
                    ) {
                        languages.forEach { language ->
                            DropdownMenuItem(
                                text = { Text(language) },
                                onClick = {
                                    selectedLanguage = language
                                    isLanguageDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                //Dropdown for Level
                Box {
                    Text(
                        text = if (selectedLevel.isEmpty()) "Select a Level" else selectedLevel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black, MaterialTheme.shapes.small)
                            .padding(8.dp)
                            .clickable { isLevelDropdownExpanded = true }
                    )
                    DropdownMenu(
                        expanded = isLevelDropdownExpanded,
                        onDismissRequest = { isLevelDropdownExpanded = false }
                    ) {
                        levels.forEach { level ->
                            DropdownMenuItem(
                                text = { Text(level) },
                                onClick = {
                                    selectedLevel = level
                                    isLevelDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                //Submit Button
                Button(
                    onClick = {
                        if (selectedLanguage.isNotEmpty() && selectedLevel.isNotEmpty()) {
                            onSelectionComplete(selectedLanguage, selectedLevel)
                            navController.navigate("preparing_class")
                        }
                    },
                    enabled = selectedLanguage.isNotEmpty() && selectedLevel.isNotEmpty()
                ) {
                    Text("Start Class")
                }
            }
        }
    }
}