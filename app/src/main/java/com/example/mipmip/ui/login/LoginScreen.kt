package com.example.mipmip.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mipmip.ui.AuthenticationState
import com.example.mipmip.utils.Colors


@Composable
fun LoginScreen(
    navController: NavController,
    onNavigateToMainScreen: () -> Unit = {},
    loginViewModel: LoginViewModel = viewModel()
) {
    LoginScreenTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(30.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val authenticationState: AuthenticationState by loginViewModel.authenticationState.collectAsState()
                when (authenticationState) {
                    AuthenticationState.LoggedIn -> {
                        LaunchedEffect(authenticationState) {
                            onNavigateToMainScreen()
                        }
                    }
                    AuthenticationState.VerifyPhoneNum -> {
                        VerificationPhoneNumLabel(loginViewModel)
                    }
                    AuthenticationState.VerifyOtp -> {
                        VerificationOtpLabel(loginViewModel)
                    }
                    AuthenticationState.EditProfile -> {
                        EditProfileLabel(loginViewModel)
                    }
                    else -> {
                        //Loading
                    }
                }
            }
        }
    }
}


@Composable
private fun LoginScreenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = Colors.myLoginThemeColors,
        content = content,
    )
}