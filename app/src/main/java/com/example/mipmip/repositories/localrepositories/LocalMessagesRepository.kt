package com.example.mipmip.repositories.localrepositories

import com.example.mipmip.dao.MessageDao
import com.example.mipmip.models.Message
import javax.inject.Inject

class LocalMessagesRepository @Inject constructor(
    private val messageDao: MessageDao
):ILocalMessagesRepository {
    override suspend fun fetchContactMessagesLocally(contactPhoneNum: String):MutableList<Message> {
        return messageDao.getLastLocalMessage(contactPhoneNum)
    }

    override suspend fun fetchLastMessagesLocally(): MutableList<Message> {
        return messageDao.getLatestMessagesForEachContact()
    }

    override suspend fun insertMessageLocally(message: Message) {
        messageDao.addNewMessage(message = message)
    }

    override suspend fun insertMessageListLocally(messages: List<Message>) {
        messageDao.insertMessagesList(messages)
    }
}