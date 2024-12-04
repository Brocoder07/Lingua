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
import com.google.gson.Gson

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
            ChatHistoryScreen(navController = navController, chatViewModel = chatViewModel)
        }
        composable("chat_detail/{chatId}") { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            ChatDetailScreen(chatId = chatId, chatViewModel = chatViewModel, navController = navController)
        }
        composable("language_selection") {
            LanguageSelectionScreen(navController = navController) { language, level ->
                //Pass selected language and level to the new chat
                val newChat = Chat(
                    id = "Chat ${chatViewModel.chatHistory.size + 1}",
                    language = language,
                    level = level
                )
                chatViewModel.addChat(newChat)
            }
        }
        composable("preparing_class") {
            PreparingClassScreen(navController = navController)
        }
        composable(
            "mcq_test/{questions}/{answers}",
            arguments = listOf(
                navArgument("questions") { type = NavType.StringType },
                navArgument("answers") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val questionsJson = backStackEntry.arguments?.getString("questions")
            val answersJson = backStackEntry.arguments?.getString("answers")

            val questions = Gson().fromJson(questionsJson, Array<String>::class.java).toList()
            val answers = Gson().fromJson(answersJson, Array<Array<String>>::class.java).map { it.toList() }

            MCQTestScreen(
                navController = navController,
                questions = questions,
                answers = answers,
                onSubmit = { selectedAnswers ->
                    //Handling submitted answers
                }
            )
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
