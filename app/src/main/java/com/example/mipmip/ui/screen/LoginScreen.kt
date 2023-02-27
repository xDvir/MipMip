package com.example.mipmip.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mipmip.ui.Colors
import com.example.mipmip.R
import com.example.mipmip.viewmodels.LoginViewModel

class LoginScreen {

    private val loginViewModel: LoginViewModel = LoginViewModel()

    @Composable
    fun LoginScreenUI() {
        LoginScreenTheme {
            Surface {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(30.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    VerificationLabel()
                    InputPhoneComponent()
                }
            }
        }
    }

    @Composable
    private fun VerificationLabel() {
        val image: Painter = painterResource(id = R.drawable.baseline_verified_user_24)
        Image(
            painter = image,
            contentDescription = "",
            colorFilter = ColorFilter.tint(color = MaterialTheme.colors.primary),
            modifier = Modifier.padding(bottom = 80.dp)
        )
        Text(
            text = "Verification",
            style = TextStyle(fontSize = 25.sp),
            textAlign = TextAlign.Center
        )
        Text(
            text = "We will send you One Time Code on your phone number",
            style = TextStyle(fontSize = 14.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 20.dp)
        )
    }

    @Composable
    private fun InputPhoneComponent() {
        val phoneText:String by loginViewModel.phoneNumText.collectAsState()
        val phoneNumTextIsInValid:Boolean by loginViewModel.phoneNumTextIsValid.collectAsState()

        TextField(
            modifier = Modifier
                .padding(top = 30.dp)
                .fillMaxWidth(),
            value = phoneText,
            onValueChange = {
                loginViewModel.onPhoneTextChanged(it)
            },
            placeholder  = {
                    Text(text = "Enter phone Number!")
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            singleLine = true,
            isError = !phoneNumTextIsInValid,
        )

        Button(
            onClick = { loginViewModel.registerNewUser()},
            modifier = Modifier
                .padding(top = 10.dp)
                .width(200.dp)
                .height(50.dp)
        )
        {
            Text(
                text = "GET OPT!",
                style = TextStyle(textDirection = TextDirection.Ltr)
            )
        }
    }

    @Composable
    private  fun InputOtpComponent() {
        var phoneNum by remember { mutableStateOf("") }
        var isFocus by remember { mutableStateOf(false) }

        TextField(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .onFocusChanged { isFocus = !isFocus },
            value = phoneNum,
            onValueChange = {
                phoneNum = it
            },

            label = {
                if (isFocus) {
                    Text("Enter phone number..")
                }
            },
        )

        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
                .height(50.dp)
        )
        {
            Text("GET OTP!")
        }
    }

    @Composable
    private fun LoginScreenTheme(content: @Composable () -> Unit) {
        MaterialTheme(
            colors = Colors.myLoginThemeColors,
            content = content,
        )
    }
}