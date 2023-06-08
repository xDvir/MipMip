package com.example.mipmip.ui.login

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.mipmip.R
import com.example.mipmip.utils.customBlueColor


@Composable
fun EditProfileLabel(loginViewModel: LoginViewModel) {
    val progressIndicatorIsVisible: Boolean by loginViewModel.progressIndicatorIsVisible.collectAsState()
    val remoteLocalUri: Uri? by loginViewModel.remoteImageUriProfile.collectAsState()
    val imageIsValid: String by loginViewModel.nextStepResult.collectAsState()
    val localImageUri = remember { mutableStateOf<Uri?>(null) }
    val doneLoading = remember { mutableStateOf(false) }
    val colorFilter = remember {
        mutableStateOf<ColorFilter?>(remoteLocalUri.let {
            ColorFilter.tint(customBlueColor)
        })
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            localImageUri.value = uri
            colorFilter.value = null
        }
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 300.dp)
                .size(200.dp)
        ) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .clip(CircleShape)
                    .fillMaxSize()
                    .clickable(enabled = true, onClick = {}),
                model = (localImageUri.value ?: remoteLocalUri ?: ""),
                onError = { if (remoteLocalUri != null) doneLoading.value = true },
                onSuccess = { doneLoading.value = true },
                loading = { OnLoadingAndErrorImage() },
                error = { OnLoadingAndErrorImage() },
                contentScale = ContentScale.Crop,
                contentDescription = ""
            )
            if (doneLoading.value) {
                Image(
                    painter = painterResource(R.drawable.baseline_edit_24),
                    contentDescription = "Edit Profile",
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(color = colorResource(id = R.color.shamrock_green))
                        .align(Alignment.TopStart)
                        .clickable(enabled = true, onClick = { galleryLauncher.launch("image/*") }),
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.TopEnd),
                    color = colorResource(id = R.color.shamrock_green),
                    strokeWidth = 3.dp
                )
            }
        }

        Text(
            text = "Update your profile picture here.",
            fontSize = 18.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 20.dp),

            )
        Button(
            onClick = {
                if (doneLoading.value) {
                    if (localImageUri.value != null) {
                        loginViewModel.uploadProfileImageAndContinue(localImageUri.value)
                    } else {
                        loginViewModel.saveLocalImageAndContinue(remoteLocalUri!!)
                    }
                }
            },
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
            style = TextStyle(fontSize = 12.sp),
            textAlign = TextAlign.Center,
            color = Color.Red,
            modifier = Modifier.padding(top = 20.dp),
            text = imageIsValid
        )
    }

}

@Composable
private fun OnLoadingAndErrorImage() {
    Image(
        painter = painterResource(R.drawable.ic_user_avatar_filled_large),
        contentDescription = "",
        contentScale = ContentScale.Crop,
        colorFilter = ColorFilter.tint(color = colorResource(id = R.color.custom_blue_color)),
        modifier = Modifier
            .clip(CircleShape)
            .fillMaxSize()
            .clickable(enabled = true, onClick = {}),
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

