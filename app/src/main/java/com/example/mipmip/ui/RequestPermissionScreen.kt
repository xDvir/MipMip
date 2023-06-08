@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.mipmip.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.mipmip.utils.Colors
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState

@Composable
fun RequestPermissionScreen(permissionName:String,permissionState: PermissionState) {
        val image: Painter = painterResource(id = R.drawable.baseline_verified_user_24)
    RequestPermissionScreenTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(30.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = image,
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(color = colorResource(id = R.color.custom_blue_color)),
                    modifier = Modifier.padding(bottom = 80.dp)
                )
                Text(
                    text = "$permissionName permission required for this feature to be available. Please grant the permission",
                    style = TextStyle(fontSize = 25.sp),
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = { permissionState.launchPermissionRequest() },
                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.custom_blue_color)),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                        .height(50.dp),

                ) {
                    Text("Request permission",color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun RequestPermissionScreenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = Colors.myLoginThemeColors,
        content = content,
    )
}