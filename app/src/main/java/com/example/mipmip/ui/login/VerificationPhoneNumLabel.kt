package com.example.mipmip.ui.login

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mipmip.R
import com.example.mipmip.ui.MainActivity
import java.lang.ref.WeakReference


@Composable
fun VerificationPhoneNumLabel(loginViewModel: LoginViewModel) {
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
        text = "We will send you One Time Password on your phone number",
        style = TextStyle(fontSize = 14.sp),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 20.dp)
    )
    InputPhoneComponent(loginViewModel)
}


@Composable
private fun InputPhoneComponent(loginViewModel: LoginViewModel) {
    val mainActivityWR = WeakReference<Activity>(LocalContext.current as MainActivity)
    val phoneNumIsInValid: Boolean by loginViewModel.inputIsValid.collectAsState()
    val phoneNumVerifyResult: String by loginViewModel.nextStepResult.collectAsState()
    val progressIndicatorIsVisible: Boolean by loginViewModel.progressIndicatorIsVisible.collectAsState()

    var phoneNum by rememberSaveable { mutableStateOf("") }
    TextField(
        modifier = Modifier
            .padding(top = 30.dp)
            .fillMaxWidth(),
        value = phoneNum,
        onValueChange = {
            phoneNum = it
        },
        placeholder = {
            Text(text = "Enter phone Number!")
        },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        singleLine = true,
        isError = !phoneNumIsInValid,
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = colorResource(id = R.color.custom_blue_color),
            unfocusedIndicatorColor = colorResource(id = R.color.custom_blue_color)
        ),
    )

    Button(
        onClick = { loginViewModel.registerNewUser(phoneNum, mainActivityWR) },
        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.custom_blue_color)),
        modifier = Modifier
            .padding(top = 10.dp)
            .width(200.dp)
            .height(50.dp)
    )
    {
        if (progressIndicatorIsVisible) {
            CustomCircularProgressBar()
        } else {
            Text(
                text = "Verify!",
                style = TextStyle(textDirection = TextDirection.Ltr),
                color = Color.White
            )
        }
    }
    Text(
        style = TextStyle(fontSize = 14.sp),
        textAlign = TextAlign.Center,
        color = Color.Red,
        modifier = Modifier.padding(top = 20.dp),
        text = phoneNumVerifyResult
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
