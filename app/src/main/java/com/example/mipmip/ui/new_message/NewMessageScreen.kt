package com.example.mipmip.ui.new_message

import android.Manifest
import android.net.Uri
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mipmip.R
import com.example.mipmip.models.ContactDetails
import com.example.mipmip.ui.RequestPermissionScreen
import com.example.mipmip.ui.Screen
import com.example.mipmip.utils.Colors
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.reflect.KSuspendFunction1

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NewMessageScreen(
    navController: NavController,
    newMessageViewModel: NewMessageViewModel = viewModel(),
    phoneNum: String
) {
    val isDoneLoadingContacts: Boolean by newMessageViewModel.isDoneLoadingContacts.collectAsState()

    NewMessageScreenTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                val permissionState = rememberPermissionState(Manifest.permission.READ_CONTACTS)
                TopAppBar(
                    isDoneLoadingContacts = isDoneLoadingContacts,
                    searchQuery = { newMessageViewModel.searchContactsByName(it) },
                    permissionState.status.isGranted)
                if (permissionState.status.isGranted) {
                    val newMessageScreenScope = rememberCoroutineScope()
                    LaunchedEffect(Unit){
                        newMessageViewModel.fetchContactsFromLocal(false)
                    }
                    Column {
                        ActiveContactsList(navController = navController,activeList = newMessageViewModel.activeFilterContactListDetails)
                        RefreshFloatingButton(newMessageViewModel::fetchContactsFromLocal,newMessageScreenScope)
                    }
                } else {
                    if (permissionState.status.shouldShowRationale) {
                        RequestPermissionScreen("Read Contacts",permissionState)
                    }
                    else{
                        LaunchedEffect(Unit) {
                            permissionState.launchPermissionRequest()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveContactsList(
    navController: NavController,
    activeList: List<ContactDetails>,
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(
            items = activeList,
            itemContent = {
                ContactListItem(contactDetails = it, onNavigationToChatScreen = {
                    navController.navigate(
                        Screen.ChatScreen.withArgs(it.phoneNum,it.contactName, Uri.encode(it.imageUri.toString()))) {
                        popUpTo(Screen.MainScreen.withArgs())
                    }
                })
            })
    }
}

@Composable
fun ContactListItem(contactDetails: ContactDetails, onNavigationToChatScreen: () -> Unit) {
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
                imageModel = contactDetails.imageUri?.toUri() ?: "",
                contentScale = ContentScale.Crop,
                circularReveal = CircularReveal(duration = 250),
                placeHolder = ImageBitmap.imageResource(R.drawable.ic_user_avatar_filled),
                error = ImageBitmap.imageResource(R.drawable.ic_user_avatar_filled)
            )
            Text(
                text = contactDetails.contactName,
                modifier = Modifier.padding(top = 10.dp, start = 8.dp),
                style = MaterialTheme.typography.h6
            )
        }
    }
}


@Composable
fun TopAppBar(isDoneLoadingContacts: Boolean, searchQuery: (queryText: String) -> Unit,contactPermissionState: Boolean) {
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
            if(contactPermissionState) {
                SearchTextField(isSearchFocus, searchQuery)
                if (!isDoneLoadingContacts) {
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
private fun CustomCircularProgressBar() {
    CircularProgressIndicator(
        modifier = Modifier.size(20.dp),
        color = Color.White,
        strokeWidth = 2.dp,
    )
}

@Composable
private fun NewMessageScreenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = Colors.myLoginThemeColors,
        content = content,
    )
}

@Composable
fun RefreshFloatingButton(
    fetchContactsFromLocal: KSuspendFunction1<Boolean, Unit>,
    newMessageScreenScope: CoroutineScope
) {
    val newMessageIcon: Painter = painterResource(id = R.drawable.baseline_refresh_24)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = {newMessageScreenScope.launch {fetchContactsFromLocal(true)}},
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