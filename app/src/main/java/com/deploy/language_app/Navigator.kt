package com.deploy.language_app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun Navigator(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel(),
    chatViewModel: ChatViewModel = viewModel()
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.observeAsState()
    val startDestination = if (authState is AuthState.Authenticated) "home" else "welcome"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("welcome") {
            WelcomePage(navController = navController)
        }
        composable("login") {
            LoginPage(modifier, navController, authViewModel)
        }
        composable("signup") {
            SignUp(modifier, navController, authViewModel)
        }
        composable("home") {
            HomePage(modifier, navController, authViewModel)
        }
        composable("chat") {
            ChatHistoryScreen(navController = navController, chatViewModel = chatViewModel, authViewModel = authViewModel)
        }
        composable("chat_detail/{chatId}") { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            ChatDetailScreen(chatId = chatId, chatViewModel = chatViewModel, authViewModel = authViewModel, navController = navController)
        }
        composable("language_selection") {
            LanguageSelectionScreen(navController = navController) { language, level, title ->
                //Pass selected language and level to the new chat
                val newChat = authViewModel.userData.value?.let { it1 ->
                    chatViewModel.createChat(
                        userId = it1.user_id,
                        language = language,
                        level = level,
                        title = title,
                    )
                }
            }
        }
        composable("preparing_class") {
            PreparingClassScreen(navController = navController)
        }
        composable(
            "mcq_test/{chatId}",
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            MCQTestScreen(
                navController = navController,
                chatId = chatId,
                chatViewModel = chatViewModel,
                authViewModel = authViewModel
            )
        }
        composable(
            "test_result/{score}",
            arguments = listOf(
                navArgument("score") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getString("score") ?: "0"
            TestResultScreen(navController = navController, score = score)
        }
    }
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            navController.navigate("home") {
                popUpTo("welcome") { inclusive = true }
            }
        }
    }
}
