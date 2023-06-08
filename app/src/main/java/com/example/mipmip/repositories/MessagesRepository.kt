package com.example.mipmip.repositories

import com.example.mipmip.models.Message
import com.example.mipmip.repositories.localrepositories.ILocalMessagesRepository
import com.example.mipmip.repositories.remoterepositories.remoteInterfaces.IRemoteMessagesRepository
import com.example.mipmip.repositories.repositoriesInterfaces.IMessagesRepository
import javax.inject.Inject


class MessagesRepository @Inject constructor(
   private val remoteMessagesRepository: IRemoteMessagesRepository,
   private val localMessagesRepository: ILocalMessagesRepository
) : IMessagesRepository {

    override suspend fun sendMessage(newMessage: Message,myPhoneNum:String,contactPhoneNum: String,sendMessageIRepositoryResult: IRepositoryResult){
        remoteMessagesRepository.sendMessage(newMessage,myPhoneNum,contactPhoneNum,sendMessageIRepositoryResult)
    }

    override suspend fun fetchContactMessagesLocally(contactPhoneNum: String): MutableList<Message> {
        return localMessagesRepository.fetchContactMessagesLocally(contactPhoneNum)
    }

    override suspend fun fetchContactMessages(myPhoneNum: String,contactPhoneNum: String,lastLocalMessage:Message?,fetchMessagesIRepositoryResult:IRepositoryResult) {
        remoteMessagesRepository.fetchMessagesRemote(myPhoneNum,contactPhoneNum,lastLocalMessage,fetchMessagesIRepositoryResult)
    }


    override suspend fun insertMessageLocally(message: Message) {
        localMessagesRepository.insertMessageLocally(message = message)
    }

    override suspend fun insertMessageListLocally(messages: List<Message>) {
        localMessagesRepository.insertMessageListLocally(messages = messages)
    }

    override suspend fun fetchLastMessagesRemote(myPhoneNum: String, fetchLastMessagesIRepositoryResult:IRepositoryResult) {
        remoteMessagesRepository.fetchLastMessagesRemote(myPhoneNum,fetchLastMessagesIRepositoryResult)
    }

    override suspend fun fetchLastMessagesLocally(myPhoneNum: String):MutableList<Message> {
        return localMessagesRepository.fetchLastMessagesLocally()
    }

    override suspend fun setListenerToNewMessage(myPhoneNum:String, onNewMessageAction: IListenerAction<Message>) {
        remoteMessagesRepository.setListenerToNewMessage(myPhoneNum,onNewMessageAction)
    }

    override fun updateMessageToRead(myPhoneNum: String,contactPhoneNum:String,unReadMessagesList: MutableList<Message>) {
        remoteMessagesRepository.updateMessageToRead(myPhoneNum,contactPhoneNum,unReadMessagesList)
    }

    override suspend fun setNewMessageAddedToConversationsListener(
        myPhoneNum: String,
        contactPhoneNum: String,
        onNewMessageAction: IListenerAction<Message>
    ) {
        remoteMessagesRepository.setNewMessageAddedToConversationsListener(myPhoneNum,contactPhoneNum,onNewMessageAction)
    }

    override fun removeNewMessageAddedToConversationsListener(
        myPhoneNum: String,
        contactPhoneNum: String
    ) {
        remoteMessagesRepository.removeNewMessageAddedToConversationsListener(myPhoneNum,contactPhoneNum)
    }

    override suspend fun updateLastMessageToRead(myPhoneNum: String, contactPhoneNum: String){
        remoteMessagesRepository.updateLastMessageToRead(myPhoneNum,contactPhoneNum)
    }

}

