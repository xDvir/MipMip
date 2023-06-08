package com.example.mipmip.ui.chat

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mipmip.R
import com.example.mipmip.models.Message
import com.example.mipmip.utils.Colors
import com.example.mipmip.utils.customBlueColor
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

@Composable
fun ChatScreen(
    navController: NavController,
    chatViewModel: ChatViewModel = viewModel(),
    contactPhoneNum: String,
    contactName: String,
    imageUri: String?
) {
    ChatScreenTheme {
        val isLandscape =
            LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                LaunchedEffect(contactPhoneNum) {
                    withContext(IO) {
                        chatViewModel.fetchMessages(contactPhoneNum)
                    }
                }
                var componentWeight = 0.9f;
                if (isLandscape) {
                    componentWeight = 0.8f;
                }
                TopAppBar(contactName, imageUri)
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(componentWeight)
                            .fillMaxHeight()
                    ) {
                        MessageDisplay(
                            messagesList = chatViewModel.messagesList.reversed(),
                            contactPhoneNum = contactPhoneNum,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1 - componentWeight)
                            .fillMaxHeight()
                    ) {
                        SendMessageComponent(
                            contactPhoneNum = contactPhoneNum,
                            sendMessage = chatViewModel::sendMessage,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopAppBar(contactName: String, imageUri: String?) {
    TopAppBar(
        title = {
            GlideImage(
                modifier = Modifier
                    .width(45.dp)
                    .height(45.dp)
                    .clip(CircleShape)
                    .clickable(enabled = true) {
                    },

                imageModel = imageUri?.toUri() ?: "",
                contentScale = ContentScale.Crop,
                circularReveal = CircularReveal(duration = 250),
                placeHolder = ImageBitmap.imageResource(R.drawable.ic_user_avatar_filled),
                error = ImageBitmap.imageResource(R.drawable.ic_user_avatar_filled)
            )
            Text(
                text = contactName,
                modifier = Modifier.padding(top = 4.dp, start = 8.dp),
                style = MaterialTheme.typography.h6
            )
        },
        backgroundColor = colorResource(id = R.color.custom_blue_color),
        contentColor = MaterialTheme.colors.onPrimary,
        elevation = 8.dp,
        actions = {
            IconButton(onClick = { }) {
            }
        }
    )
}

@Composable
fun MessageDisplay(
    messagesList: List<Message>,
    contactPhoneNum: String,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        MessagesList(messagesList, contactPhoneNum)
    }
}


@Composable
private fun MessagesList(
    messagesList: List<Message>,
    contactPhoneNum: String,
) {
    val messageListState = rememberLazyListState()
    LaunchedEffect(messagesList) {
        if (messagesList.isNotEmpty()) {
            messageListState.scrollToItem(index = messagesList.size - 1)
        }
    }
    LazyColumn(
        state = messageListState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(
            items = messagesList,
            itemContent = {
                MessageItem(message = it, contactPhoneNum)
            })
    }
}


@Composable
fun MessageItem(message: Message, contactPhoneNum: String) {
    val contentAlignment: Alignment
    val backgroundColor: Color
    val backgroundScale: Float
    if (message.sender == contactPhoneNum) {
        contentAlignment = Alignment.CenterEnd
        backgroundColor = colorResource(id = R.color.shamrock_green)
        backgroundScale = -1f
    } else {
        backgroundScale = 1f
        contentAlignment = Alignment.CenterStart
        backgroundColor = colorResource(id = R.color.custom_blue_color)
    }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = contentAlignment
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .padding(top = 10.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.message_background),
                contentDescription = "",
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer(scaleX = backgroundScale)
                    .clickable {},
                contentScale = ContentScale.FillBounds,
                colorFilter = ColorFilter.tint(backgroundColor),
            )
            Column(
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Row {
                    Spacer(
                        modifier = Modifier
                            .width(10.dp)
                    )
                    Text(
                        message.text,
                        color = Color.White,
                        modifier = Modifier.widthIn(max = 150.dp)
                    )
                    Spacer(
                        modifier = Modifier
                            .width(10.dp)
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .height(50.dp)
                    .width(20.dp)
            )
        }
    }
}


@Composable
fun SendMessageComponent(
    contactPhoneNum: String,
    sendMessage: (String, String) -> Unit,
) {
    var textMessage by rememberSaveable { mutableStateOf("") }
    Row(modifier = Modifier.padding(top = 10.dp)) {
        Spacer(modifier = Modifier.width(10.dp))
        TextField(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            value = textMessage,
            onValueChange = { text ->
                textMessage = text
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.LightGray,
                cursorColor = customBlueColor,
                focusedIndicatorColor = Color.Transparent, // Set the focused indicator color to transparent
                unfocusedIndicatorColor = Color.Transparent // Set the unfocused indicator color to transparent
            ),
            shape = RoundedCornerShape(50.dp)
        )
        Spacer(modifier = Modifier.width(7.dp))
        Image(
            painter = painterResource(R.drawable.baseline_send_24),
            contentDescription = "Edit Profile",
            contentScale = ContentScale.Inside,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(color = colorResource(id = R.color.custom_blue_color))
                .clickable(
                    enabled = true,
                    onClick = {
                        if (textMessage.isNotEmpty()) {
                            sendMessage(contactPhoneNum, textMessage)
                            textMessage = ""
                        }
                    }),
        )
    }
}

@Composable
private fun ChatScreenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = Colors.myLoginThemeColors,
        content = content,
    )
}
