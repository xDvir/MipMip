package com.example.mipmip.repositories.localrepositories

import com.example.mipmip.models.Message

interface ILocalMessagesRepository {
    suspend fun fetchContactMessagesLocally(contactPhoneNum: String):MutableList<Message>
    suspend fun fetchLastMessagesLocally():MutableList<Message>
    suspend fun insertMessageLocally(message: Message)
    suspend fun insertMessageListLocally(messages: List<Message>)
}
