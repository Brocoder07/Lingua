package com.deploy.language_app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

@Composable
fun WelcomePage(navController: NavHostController) {
    val titleVisible = remember { mutableStateOf(false) }
    val subtitleVisible = remember { mutableStateOf(false) }
    val buttonVisible = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        titleVisible.value = true
        delay(200)
        subtitleVisible.value = true
        delay(300)
        buttonVisible.value = true
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF3366FF), Color(0xFF00CCFF))
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title text with separated lines for "Welcome to" and "LanguageApp!"
            AnimatedVisibility(
                visible = titleVisible.value,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -40 })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Welcome to",
                        fontSize = 34.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.height(4.dp)) // Space between the two parts
                    Text(
                        text = "Language App!",
                        fontSize = 34.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        textAlign = TextAlign.End
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            //Subtitle text
            AnimatedVisibility(
                visible = subtitleVisible.value,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
            ) {
                Text(
                    text = "Master languages effortlessly with our AI-powered teacher",
                    fontSize = 18.sp,
                    color = Color(0xFFEFF5FC),
                    lineHeight = 28.sp,
                    modifier = Modifier.padding(horizontal = 32.dp),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(60.dp))
            //Login and signup buttons
            AnimatedVisibility(
                visible = buttonVisible.value,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 80 })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = { navController.navigate("login") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .padding(horizontal = 20.dp)
                            .shadow(10.dp, RoundedCornerShape(24.dp))
                    ) {
                        Text(
                            text = "Login",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF3366FF)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedButton(
                        onClick = { navController.navigate("signup") },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(2.dp, Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .padding(horizontal = 20.dp)
                            .shadow(10.dp,RoundedCornerShape(24.dp))
                    ) {
                        Text(
                            text = "Sign Up",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}