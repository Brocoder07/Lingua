package com.deploy.language_app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

//Navigator for handling app navigation
@Composable
fun Navigator(modifier: Modifier = Modifier, authViewModel: AuthViewModel = viewModel()) {
    //Navigation Controller
    val navController = rememberNavController()
    //Observe the authentication state
    val authState by authViewModel.authState.observeAsState()
    //Determine the start destination based on authentication status
    val startDestination = if (authState is AuthState.Authenticated) "home" else "welcome"
    //Navigation graph with dynamic start destination
    NavHost(navController = navController, startDestination = startDestination) {
        composable("welcome") {
            WelcomePage(navController = navController) //Welcome screen
        }
        composable("login") {
            LoginPage(modifier, navController, authViewModel) //Login screen
        }
        composable("signup") {
            SignUp(modifier, navController, authViewModel) //Sign Up screen
        }
        composable("home") {
            HomePage(modifier, navController,authViewModel) //Home screen
        }
        composable("chat") {
            ChatScreen(navController = navController)
        }
        composable("chat_detail/{chatId}") { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            ChatDetailScreen(chatId = chatId)
        }
    }
    //Redirect to home if the user is authenticated after login/signup
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            navController.navigate("home") {
                popUpTo("welcome") { inclusive = true }
            }
        }
    }
}
