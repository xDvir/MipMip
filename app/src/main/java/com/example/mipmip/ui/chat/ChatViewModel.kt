package com.example.mipmip.ui.chat

import android.app.Application
import android.provider.Settings.Global
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mipmip.models.Message
import com.example.mipmip.repositories.IListenerAction
import com.example.mipmip.repositories.IRepositoryResult
import com.example.mipmip.repositories.MessagesRepository
import com.example.mipmip.repositories.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    application: Application,
    private val messagesRepository: MessagesRepository,
    private val usersRepository: UsersRepository

) : AndroidViewModel(application) {

    val messagesList = mutableStateListOf<Message>()
    private var mContactPhoneNum: String = ""

    companion object {
        const val TAG = "ChatViewModel"
    }

    fun sendMessage(contactPhoneNum: String, message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val myPhoneNum = usersRepository.getMyPhoneNum()
            if (myPhoneNum != null) {
                val newMessage = Message(
                    text = message,
                    sender = myPhoneNum,
                    recipient = contactPhoneNum,
                    messageContactPhoneNum = contactPhoneNum,
                    time =  System.currentTimeMillis(),
                )
                messagesRepository.sendMessage(
                    newMessage,
                    myPhoneNum,
                    contactPhoneNum,
                    sendMessageIRepositoryResult
                )
            }
        }
    }

  suspend fun fetchMessages(contactPhoneNum: String) = withContext(Dispatchers.IO) {
          val myPhoneNum = usersRepository.getMyPhoneNum()
          if (myPhoneNum != null) {
              mContactPhoneNum = contactPhoneNum
              messagesRepository.updateLastMessageToRead(
                  myPhoneNum,
                  mContactPhoneNum)
              viewModelScope.launch(Dispatchers.IO) {
                  val localMessagesListAsync = async{messagesRepository.fetchContactMessagesLocally(contactPhoneNum)}
                  val localMessagesList = localMessagesListAsync.await()
                  val lastLocalMessage: Message? = if (localMessagesList.isNotEmpty()) {
                      withContext(Dispatchers.Main) {
                          messagesList.addAll(localMessagesList)
                      }
                      localMessagesList.first()
                  } else {
                      null
                  }
                  messagesRepository.fetchContactMessages(
                      myPhoneNum,
                      contactPhoneNum,
                      lastLocalMessage,
                      fetchMessagesIRepositoryResult
                  )
              }
          }
      }

    private val sendMessageIRepositoryResult = object : IRepositoryResult {
        override fun <T> onSuccesses(data: T?) {
            if (data != null && data is Message) {
                viewModelScope.launch(Dispatchers.IO) {
                    messagesRepository.insertMessageLocally(data as Message)
                }
            }

        }

        override fun onFailed(message: String) {
            Log.e(TAG, "Failed while sending message")
        }

    }

    private val fetchMessagesIRepositoryResult = object : IRepositoryResult {
        override fun <T> onSuccesses(data: T?) {
            val unReadMessagesList:MutableList<Message> = mutableListOf()
            val messagesListToInsert:MutableList<Message> = mutableListOf()
            val myPhoneNum = usersRepository.getMyPhoneNum()
            if(myPhoneNum !=null) {
                viewModelScope.launch(Dispatchers.IO) {
                    if (data != null && data is List<*>) {
                        for (message in data) {
                            if (message is Message) {
                                if (!message.isRead) {
                                    unReadMessagesList.add(message)
                                    message.isRead = true
                                }
                                withContext(Dispatchers.Main) {
                                    messagesList.add(0, message)
                                }
                                messagesListToInsert.add(message)
                            }
                        }
                        messagesRepository.setNewMessageAddedToConversationsListener(
                            myPhoneNum,
                            mContactPhoneNum,
                            onNewMessageAction
                        )
                        messagesRepository.insertMessageListLocally(messagesListToInsert)
                    }
                }
                messagesRepository.updateMessageToRead(
                    myPhoneNum,
                    mContactPhoneNum,
                    unReadMessagesList
                )
            }
        }

        override fun onFailed(message: String) {
            Log.e(TAG, "Failed while sending message $message")
        }

    }

    override fun onCleared() {
        super.onCleared()
        val myPhoneUser = usersRepository.getMyPhoneNum()
        val contactPhoneNum = mContactPhoneNum
        if(myPhoneUser != null && contactPhoneNum != "") {
                messagesRepository.removeNewMessageAddedToConversationsListener(myPhoneUser,contactPhoneNum)
        }
    }

    private val onNewMessageAction = object : IListenerAction<Message>{
        override fun onDataChange(data: Message) {
            val myPhoneNum = usersRepository.getMyPhoneNum()
            if(myPhoneNum!=null) {
                messagesList.add(0, data)
            }
        }


    }

}