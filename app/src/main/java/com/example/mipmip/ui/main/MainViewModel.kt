package com.example.mipmip.ui.main

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mipmip.models.ContactDetails
import com.example.mipmip.models.LastMessage
import com.example.mipmip.models.Message
import com.example.mipmip.repositories.*
import com.example.mipmip.repositories.repositoriesInterfaces.IUsersRepository
import com.example.mipmip.ui.AuthenticationState
import com.example.mipmip.ui.login.LoginViewModel
import com.example.mipmip.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: IUsersRepository,
    private val messagesRepository: MessagesRepository,
    private val contactsRepository: ContactsRepository,
    private val imageRepository: ImagesRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _userSessionState =
        MutableStateFlow<AuthenticationState>(AuthenticationState.Loading)
    private val _isDoneLoadingLastMessages = MutableStateFlow(true)
    val userSessionState: StateFlow<AuthenticationState> = _userSessionState
    val isDoneLoadingLastMessages = _isDoneLoadingLastMessages.asStateFlow()
    var userPhoneNumber: String? = null
    val messagesList: MutableList<Message> = mutableListOf()
    val lastMessagesListFilter = mutableStateListOf<LastMessage>()
    val lastMessagesListFull= mutableListOf<LastMessage>()

    companion object {
        const val TAG = "MainViewModel"
    }

    init {
        isUserLoggedIn()
    }

    suspend fun fetchLastMessages() = withContext(Dispatchers.IO) {
        _isDoneLoadingLastMessages.value = false
        viewModelScope.launch(Dispatchers.IO) {
            if (userPhoneNumber != null) {
                messagesRepository.fetchLastMessagesRemote(userPhoneNumber!!, fetchLastMessagesIRepositoryResult)
            }
        }
    }


    private fun isUserLoggedIn() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.isUserLoggedIn(isUserLoggedInListener)
        }
    }

    fun searchContactsByName(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isDoneLoadingLastMessages.value = false
            val filteredList = if (query == Constants.EMPTY_STRING) {
                lastMessagesListFull
            } else {
                val contactListTemp = lastMessagesListFull
                contactListTemp.filter { contact ->
                    contact.contactName.contains(query, ignoreCase = true)
                }
            }
            withContext(Dispatchers.Main) {
                lastMessagesListFilter.clear()
                lastMessagesListFilter.addAll(filteredList)
                _isDoneLoadingLastMessages.value = true
            }
        }
    }

    private fun setListenerToNewMessage() {
        if (userPhoneNumber != null) {
            viewModelScope.launch(Dispatchers.IO) {
                messagesRepository.setListenerToNewMessage(userPhoneNumber!!, onNewMessageAction)
            }
        }
    }

    private val onNewMessageAction = object : IListenerAction<Message> {
        override fun onDataChange(data: Message) {
            viewModelScope.launch(Dispatchers.IO) {
                messagesRepository.insertMessageLocally(data)
            }
            lastMessagesListFilter.removeIf { it.messageContactPhoneNum == data.messageContactPhoneNum }
            lastMessagesListFull.removeIf { it.messageContactPhoneNum == data.messageContactPhoneNum}
            getDetailsFromMessageAndPushToList(listOf(data))
        }

    }

    private val isUserLoggedInListener = object : IRepositoryResult {
        override fun <T> onSuccesses(data: T?) {
            if (data != null) {
                userPhoneNumber = data as String
                _userSessionState.value = AuthenticationState.LoggedIn
                setListenerToNewMessage()
            } else {
                _userSessionState.value = AuthenticationState.NotLoggedIn
            }
        }

        override fun onFailed(message: String) {
            Log.e(LoginViewModel.TAG, "Error while check if user is LoggedIn: $message")
            _userSessionState.value = AuthenticationState.NotLoggedIn
        }
    }


    private val fetchLastMessagesIRepositoryResult = object : IRepositoryResult {
        override fun <T> onSuccesses(data: T?) {
            messagesList.clear()
            lastMessagesListFilter.clear()
            if (data != null && data is List<*>) {
                for (message in data) {
                    if (message is Message) {
                        messagesList.add(message)
                    }
                }
                getDetailsFromMessageAndPushToList(messagesList)
            }
        }

        override fun onFailed(message: String) {
            Log.e(TAG, "Error")
        }

    }

    fun getDetailsFromMessageAndPushToList(updateMessageList: List<Message>) {
        viewModelScope.launch(Dispatchers.IO) {
            for (lMessage in updateMessageList) {
                val lastMessage = processMessage(lMessage)
                withContext(Dispatchers.Main) {
                   lastMessagesListFilter.add(0, lastMessage)
                  lastMessagesListFull.add(0, lastMessage)
                }
            }
            _isDoneLoadingLastMessages.value = true
        }
    }

    private suspend fun processMessage(lMessage: Message): LastMessage {
        return withContext(Dispatchers.IO) {
            val contactPhoneNum = lMessage.messageContactPhoneNum
            val contactMessage = lMessage.text
            val contactMessageIsRead = lMessage.isRead
            val contactMessageTime = lMessage.time

            val contactDetails = contactsRepository.getContactsDetailsLocally(contactPhoneNum)
            if (contactDetails != null) {
                val imageUri = Uri.parse(contactDetails.imageUri)
                LastMessage(
                    contactDetails.contactName,
                    imageUri,
                    contactMessage,
                    contactMessageIsRead,
                    contactMessageTime,
                    contactPhoneNum
                )
            } else {
                val contactNameAsync = async { contactsRepository.getContactNameByPhoneNumber(contactPhoneNum) }
                val contactImageAsync = async { imageRepository.getImageUrlByPhoneNumber(contactPhoneNum) }

                val contactName = contactNameAsync.await()
                val contactImage = contactImageAsync.await()

                val lastMessage = LastMessage(
                    contactName,
                    contactImage,
                    contactMessage,
                    contactMessageIsRead,
                    contactMessageTime,
                    contactPhoneNum
                )

                contactsRepository.updateContactsDetails(
                    ContactDetails(
                        contactPhoneNum,
                        contactName,
                        "",
                        contactImage.toString()
                    )
                )

                lastMessage
            }
        }
    }

}