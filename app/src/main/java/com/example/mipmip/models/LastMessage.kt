package com.example.mipmip.models

import android.net.Uri
import java.util.Date

data class LastMessage(
    val contactName: String,
    val contactImage: Uri?,
    var lastMessageText: String,
    var lastMessageIsRead: Boolean,
    var lastMessageTime :Long,
    var messageContactPhoneNum: String,
)
