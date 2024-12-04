package com.deploy.language_app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

@Composable
fun PreparingClassScreen(navController: NavHostController) {
    LaunchedEffect(Unit) {
        //Simulate a delay for animation
        delay(2000)
        navController.navigate("chat")
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF3366FF), Color(0xFF00CCFF))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Preparing your class :)",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
    }
}