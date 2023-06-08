package com.example.mipmip.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mipmip.ui.Screen
import com.example.mipmip.ui.chat.ChatScreen
import com.example.mipmip.ui.chat.ChatViewModel
import com.example.mipmip.ui.login.LoginScreen
import com.example.mipmip.ui.login.LoginViewModel
import com.example.mipmip.ui.main.MainScreen
import com.example.mipmip.ui.main.MainViewModel
import com.example.mipmip.ui.new_message.NewMessageScreen
import com.example.mipmip.ui.new_message.NewMessageViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.MainScreen.withArgs()) {
        composable(route = Screen.LoginScreen.route) {
            val loginViewModel = hiltViewModel<LoginViewModel>()
            LoginScreen(
                navController = navController,
                onNavigateToMainScreen = { navController.navigate(Screen.MainScreen.withArgs()) },
                loginViewModel
            )
        }
        composable(route = Screen.MainScreen.route) {
            val mainViewModel = hiltViewModel<MainViewModel>()
            MainScreen(
                navController = navController,
                onNavigateToLoginScreen = { navController.navigate(Screen.LoginScreen.withArgs()) },
                mainViewModel
            )
        }
        composable(
            route = Screen.NewMessageScreen.route + "/{phoneNum}",
            arguments = listOf(navArgument("phoneNum") {
                type = NavType.StringType
            })
        ) { entry ->
            val newMessageViewModel = hiltViewModel<NewMessageViewModel>()
            NewMessageScreen(
                navController = navController,
                newMessageViewModel = newMessageViewModel,
                phoneNum = entry.arguments?.getString("phoneNum")!!
            )
        }

        composable(
            route = Screen.ChatScreen.route + "/{phoneNum}/{contactName}/{imageUri}",
            arguments = listOf(
                navArgument("phoneNum") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("contactName") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                },
                navArgument("imageUri") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { entry ->
            val chatViewModel = hiltViewModel<ChatViewModel>()
            val phoneNum = entry.arguments?.getString("phoneNum")
            val contactName = entry.arguments?.getString("contactName")
            val imageUri = entry.arguments?.getString("imageUri")
            if (phoneNum != null && contactName != null) {
                    ChatScreen(
                        navController = navController,
                        chatViewModel = chatViewModel,
                        contactPhoneNum = phoneNum,
                        contactName = contactName,
                        imageUri = imageUri
                    )
                }
            }
        }
}