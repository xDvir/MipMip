package com.example.mipmip.ui.main

import android.Manifest
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mipmip.R
import com.example.mipmip.models.LastMessage
import com.example.mipmip.ui.AuthenticationState
import com.example.mipmip.ui.RequestPermissionScreen
import com.example.mipmip.ui.Screen
import com.example.mipmip.utils.Colors
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.glide.GlideImage
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    navController: NavController,
    onNavigateToLoginScreen: () -> Unit = {},
    mainViewModel: MainViewModel = viewModel()
) {
    MainScreenTheme {

        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                val authenticationState: AuthenticationState by mainViewModel.userSessionState.collectAsState()
                when (authenticationState) {
                    AuthenticationState.Loading -> {}
                    AuthenticationState.LoggedIn -> {
                        val isDoneLoadingMessages: Boolean by mainViewModel.isDoneLoadingLastMessages.collectAsState()
                        val permissionState =
                            rememberPermissionState(Manifest.permission.READ_CONTACTS)
                        TopAppBar(
                            isDoneLoadingMessages = isDoneLoadingMessages,
                            searchQuery = { mainViewModel.searchContactsByName(it) },
                            contactPermissionState = permissionState.status.isGranted
                        )
                        if (permissionState.status.isGranted) {
                            LaunchedEffect(Unit) {
                                mainViewModel.fetchLastMessages()
                            }
                            MainView(
                                navController,
                                mainViewModel.userPhoneNumber,
                                mainViewModel.lastMessagesListFilter
                            )
                        } else {
                            if (permissionState.status.shouldShowRationale) {
                                RequestPermissionScreen("Read Contacts", permissionState)
                            } else {
                                LaunchedEffect(Unit) {
                                    permissionState.launchPermissionRequest()
                                }
                            }
                        }
                    }
                    else -> {
                        LaunchedEffect(authenticationState) {
                            onNavigateToLoginScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopAppBar(
    isDoneLoadingMessages: Boolean,
    searchQuery: (queryText: String) -> Unit,
    contactPermissionState: Boolean
) {
    val appName = stringResource(id = R.string.app_name)
    var isSearchFocus by rememberSaveable { mutableStateOf(false) }

    TopAppBar(
        title = {
            if (!isSearchFocus) {
                Text(
                    text = appName,
                    textAlign = TextAlign.Start,
                )
            }
        },
        backgroundColor = colorResource(id = R.color.custom_blue_color),
        contentColor = MaterialTheme.colors.onPrimary,
        elevation = 8.dp,
        actions = {
            if (contactPermissionState) {
                SearchTextField(isSearchFocus, searchQuery)
                if (!isDoneLoadingMessages) {
                    CustomCircularProgressBar()
                }
                if (isSearchFocus) {
                    IconButton(onClick = { isSearchFocus = false }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Arrow Back")
                    }
                } else {
                    IconButton(onClick = { isSearchFocus = true }) {
                        Icon(Icons.Filled.Search, contentDescription = "Search Icon")
                    }
                }
            }
        }
    )
}

@Composable
private fun MainView(
    navController: NavController,
    userPhoneNumber: String?,
    lastMessageList: List<LastMessage>
) {
    LastMessagesList(navController, lastMessageList)
    NewMessageFloatingButton(navController, userPhoneNumber)
}

@Composable
fun LastMessagesList(navController: NavController, lastMessageList: List<LastMessage>) {
    LazyColumn {
        items(
            items = lastMessageList,
            itemContent = {
                LastMessageItem(lastMessage = it, onNavigationToChatScreen = {
                    navController.navigate(
                        Screen.ChatScreen.withArgs(
                            it.messageContactPhoneNum,
                            it.contactName,
                            Uri.encode(it.contactImage.toString())
                        )
                    ) {
                    }
                })
            })
    }
}

@Composable
fun LastMessageItem(lastMessage: LastMessage, onNavigationToChatScreen: () -> Unit) {
    Row(
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth()
            .clickable(enabled = true, onClick = { onNavigationToChatScreen() }),

        ) {

        Row(modifier = Modifier.padding(top = 5.dp, start = 3.dp)) {
            GlideImage(
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .clip(CircleShape)
                    .clickable(enabled = true) {

                    },
                imageModel = lastMessage.contactImage ?: "",
                contentScale = ContentScale.Crop,
                circularReveal = CircularReveal(duration = 250),
                placeHolder = ImageBitmap.imageResource(R.drawable.ic_user_avatar_filled),
                error = ImageBitmap.imageResource(R.drawable.ic_user_avatar_filled)
            )
            Column {
                Text(
                    text = lastMessage.contactName,
                    modifier = Modifier.padding(start = 8.dp, top = 5.dp),
                    style = TextStyle(fontSize = 18.sp)
                )
                Text(
                    text = lastMessage.lastMessageText,
                    modifier = Modifier.padding(start = 10.dp),
                    style = TextStyle(fontSize = 13.sp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd
            )
            {
                Column {
                    Text(
                        text = getDateString(lastMessage.lastMessageTime),
                        style = TextStyle(fontSize = 10.sp)
                    )
                    if (!lastMessage.lastMessageIsRead) {
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .background(
                                    colorResource(id = R.color.shamrock_green),
                                    shape = CircleShape
                                ),
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun NewMessageFloatingButton(navController: NavController, userPhoneNumber: String?) {
    val newMessageIcon: Painter = painterResource(id = R.drawable.baseline_message_24)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = {
                if (userPhoneNumber != null) navController.navigate(
                    Screen.NewMessageScreen.withArgs(
                        userPhoneNumber
                    )
                )
            },
            backgroundColor = colorResource(id = R.color.custom_blue_color),
            contentColor = MaterialTheme.colors.onPrimary,
            modifier = Modifier.offset(x = (-20).dp, y = (-20).dp)

        ) {
            Icon(newMessageIcon, contentDescription = "Send")
        }
    }
}

@Composable
private fun SearchTextField(isSearchFocus: Boolean, searchQuery: (queryText: String) -> Unit) {
    if (isSearchFocus) {
        val focusRequester = FocusRequester()
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
        var text by remember { mutableStateOf(TextFieldValue("")) }
        OutlinedTextField(
            modifier = Modifier
                .focusRequester(focusRequester),
            value = text,
            onValueChange = {
                text = it
                searchQuery(it.text)
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = colorResource(id = R.color.custom_blue_color),
                unfocusedIndicatorColor = colorResource(id = R.color.custom_blue_color)
            ),
        )
    }
}

@Composable
private fun CustomCircularProgressBar() {
    CircularProgressIndicator(
        modifier = Modifier.size(20.dp),
        color = Color.White,
        strokeWidth = 2.dp,
    )
}

@Composable
private fun MainScreenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = Colors.myLoginThemeColors,
        content = content,
    )
}

fun getDateString(timestamp: Long): String {
    val date = Date(timestamp)
    val dateFormat = if (hasDayPassed(timestamp)) {
        SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    } else {
        SimpleDateFormat("HH:mm", Locale.getDefault())
    }
    return dateFormat.format(date)
}

fun hasDayPassed(timestamp: Long): Boolean {
    val date = Date(timestamp)
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -1)
    val oneDayAgo = calendar.time
    return date.before(oneDayAgo)
}
