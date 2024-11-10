package com.deploy.language_app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
/* HomePage function displays the main screen after login,allowing the user to
select a language and navigate further.Includes a sign-out option to navigate back to welcome screen. */
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    var expanded by remember { mutableStateOf(false) }//State variable to manage dropdown menu visibility
    var selectedLanguage by remember { mutableStateOf("Select a language") }//State variable to hold selected language
    val languages = listOf("English", "Spanish", "Mandarin", "French", "German", "Japanese", "Russian", "Hindi")//List of languages to be displayed in the dropdown menu
    //Main container that fills the entire screen and applies a vertical gradient background
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
        //Column to arrange elements vertically in the center of the screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Main title text for the HomePage
            Text(
                "Explore Languages",
                fontSize = 28.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            //Subtitle text providing instruction to the user
            Text(
                "Select a language to start learning",
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            //Dropdown Menu
            Box(modifier = Modifier.fillMaxWidth(0.8f)) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(selectedLanguage, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    languages.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(language, fontSize = 16.sp) },
                            onClick = {
                                selectedLanguage = language
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {/* Navigate to Chatbot */},
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(52.dp)
                    .shadow(10.dp, RoundedCornerShape(24.dp))
            ) {
                Text("Continue", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF3366FF))
            }
            Spacer(modifier = Modifier.height(24.dp))
            //Sign-out Button
            OutlinedButton(
                onClick = {
                    authViewModel.signout()
                    navController.navigate("welcome") {
                        popUpTo("home") { inclusive = true } //Clear backstack to avoid returning to Home
                    }
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(2.dp, Color.White),
                modifier = Modifier
                    .fillMaxWidth(0.77f)
                    .height(52.dp)
                    .padding(horizontal = 20.dp)
                    .shadow(10.dp, RoundedCornerShape(24.dp))
            ) {
                Text("Sign Out", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}