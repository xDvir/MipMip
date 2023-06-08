package com.example.mipmip.repositories.remoterepositories.remoteInterfaces

import com.example.mipmip.models.Message
import com.example.mipmip.repositories.IListenerAction
import com.example.mipmip.repositories.IRepositoryResult

interface IRemoteMessagesRepository{

     suspend fun fetchMessagesRemote(myPhoneNum: String, contactPhoneNum: String, lastLocalMessage:Message?, fetchMessagesIRepositoryResult:IRepositoryResult)
     suspend fun sendMessage(newMessage: Message,myPhoneNum:String,contactPhoneNum: String,sendMessageIRepositoryResult: IRepositoryResult)
     suspend fun fetchLastMessagesRemote(myPhoneNum: String, fetchLastMessagesIRepositoryResult:IRepositoryResult)
     suspend fun setListenerToNewMessage(myPhoneNum:String,onNewMessageAction: IListenerAction<Message>)
     suspend  fun setNewMessageAddedToConversationsListener(myPhoneNum: String, contactPhoneNum: String, onNewMessageAction: IListenerAction<Message>)
     suspend fun updateLastMessageToRead(myPhoneNum: String, contactPhoneNum: String)
     fun removeNewMessageAddedToConversationsListener(myPhoneNum: String, contactPhoneNum: String)
     fun updateMessageToRead(myPhoneNum: String,contactPhoneNum:String, unReadMessagesList: MutableList<Message>)
}