package com.example.mipmip.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mipmip.R


@Composable
fun VerificationOtpLabel(loginViewModel: LoginViewModel) {
    val image: Painter = painterResource(id = R.drawable.baseline_verified_user_24)
    Image(
        painter = image,
        contentDescription = "",
        colorFilter = ColorFilter.tint(color = colorResource(id = R.color.custom_blue_color)),
        modifier = Modifier.padding(bottom = 80.dp)
    )
    Text(
        text = "Verification",
        style = TextStyle(fontSize = 25.sp),
        textAlign = TextAlign.Center
    )
    Text(
        text = "You will get a OTP via SMS",
        style = TextStyle(fontSize = 14.sp),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 20.dp)
    )
    InputOtpComponent(loginViewModel)
}

@Composable
private fun InputOtpComponent(loginViewModel: LoginViewModel) {
    var otp by rememberSaveable { mutableStateOf("") }
    var isFocus by rememberSaveable { mutableStateOf(true) }
    val progressIndicatorIsVisible: Boolean by loginViewModel.progressIndicatorIsVisible.collectAsState()
    val otpIsValid: Boolean by loginViewModel.inputIsValid.collectAsState()
    val otpVerifyResult: String by loginViewModel.nextStepResult.collectAsState()

    TextField(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .onFocusChanged { isFocus = !isFocus },
        value = otp,
        onValueChange = {
            otp = it
        },
        isError = !otpIsValid,
        label = {
            if (!isFocus) {
                Text("Enter OTP")
            }
        },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = colorResource(id = R.color.custom_blue_color),
            unfocusedIndicatorColor = colorResource(id = R.color.custom_blue_color)
        ),
    )

    Button(
        onClick = { loginViewModel.verifyOTP(otp) },
        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.custom_blue_color)),
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth()
            .height(50.dp)
    )
    {
        if (progressIndicatorIsVisible) {
            CustomCircularProgressBar()
        } else {
            Text(text = "Go!", color = Color.White)
        }
    }
    Text(
        style = TextStyle(fontSize = 14.sp),
        textAlign = TextAlign.Center,
        color = Color.Red,
        modifier = Modifier.padding(top = 20.dp),
        text = otpVerifyResult
    )
}
@Composable
private fun CustomCircularProgressBar() {
    CircularProgressIndicator(
        modifier = Modifier.size(30.dp),
        color = Color.White,
        strokeWidth = 3.dp
    )
}