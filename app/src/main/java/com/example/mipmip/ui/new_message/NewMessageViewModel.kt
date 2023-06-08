package com.example.mipmip.ui.new_message

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mipmip.models.ContactDetails
import com.example.mipmip.repositories.IRepositoryResult
import com.example.mipmip.repositories.repositoriesInterfaces.IContactsRepository
import com.example.mipmip.repositories.repositoriesInterfaces.IImagesRepository
import com.example.mipmip.repositories.repositoriesInterfaces.IUsersRepository
import com.example.mipmip.utils.Constants
import com.example.mipmip.utils.Constants.EMPTY_STRING
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NewMessageViewModel @Inject constructor(
    private val usersRepository: IUsersRepository,
    private val contactsRepository: IContactsRepository,
    private val imagesRepository: IImagesRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _isDoneLoadingContacts = MutableStateFlow(false)
    val isDoneLoadingContacts = _isDoneLoadingContacts.asStateFlow()
    var activeFilterContactListDetails = mutableStateListOf<ContactDetails>()
    var activeContactListFullListDetails = mutableListOf<ContactDetails>()
    val phoneNumToContacts: HashMap<String, ContactDetails> = HashMap()

    companion object {
        const val TAG = "NewMessageViewModel"
    }

    fun searchContactsByName(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isDoneLoadingContacts.value = false
            val filteredList = if (query == EMPTY_STRING) {
                activeContactListFullListDetails
            } else {
                val contactListTemp = activeContactListFullListDetails
                contactListTemp.filter { contact ->
                    contact.contactName.contains(query, ignoreCase = true)
                }
            }
            withContext(Dispatchers.Main) {
                activeFilterContactListDetails.clear()
                activeFilterContactListDetails.addAll(filteredList)
                _isDoneLoadingContacts.value = true
            }
        }
    }

    suspend fun fetchContactsFromLocal(refreshAllContacts:Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            _isDoneLoadingContacts.value = false
            val myPhoneNum = usersRepository.getMyPhoneNum()
            if (myPhoneNum != null) {
                activeFilterContactListDetails.clear()
                val contactList = contactsRepository.getAllLocalContacts(myPhoneNum)
                if (contactList.isEmpty() || refreshAllContacts) {
                    fetchAllContacts()
                } else {
                    withContext(Dispatchers.Main) {
                        activeFilterContactListDetails.addAll(contactList)
                        activeContactListFullListDetails.addAll(contactList)
                    }
                }
            }
            _isDoneLoadingContacts.value = true
        }

    }

    private fun fetchAllContacts() {
        viewModelScope.launch(Dispatchers.IO) {
            _isDoneLoadingContacts.value= false
            val myPhoneNum: String? = usersRepository.getMyPhoneNum()
            val contactsListAsync = async { contactsRepository.getPhoneContacts() }
            val contactNumbersAsync = async { contactsRepository.getContactNumbers() }
            val contactsList = contactsListAsync.await()
            val contactsNumbers = contactNumbersAsync.await()
            var countValidContact = 0
            contactsList.forEach { contact ->
                contactsNumbers[contact.contactId]?.let { numbers ->
                    var contactPhoneNum = numbers[0]
                    if (phoneNumberIsValid(contactPhoneNum)) {
                        contactPhoneNum = modifyPhoneNumberPrefix(contactPhoneNum)
                        if (contactPhoneNum != myPhoneNum) {
                            val contactDetails =
                                ContactDetails(
                                    contactPhoneNum,
                                    contact.contactName,
                                    contact.contactId,
                                )
                            phoneNumToContacts[contactPhoneNum] = contactDetails
                            countValidContact++
                            imagesRepository.getContactImageUrl(
                                contactPhoneNum,
                                getImageUrlResultListener(contactDetails, countValidContact)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun phoneNumberIsValid(phoneNum: String): Boolean {
        return Constants.REGULAR_PHONE_NUM_PATTERN.matches(phoneNum) || Constants.ISRAEL_PHONE_NUM_PATTERN.matches(
            phoneNum
        )
    }

    private fun modifyPhoneNumberPrefix(phoneNum: String): String {
        return if (Constants.ISRAEL_PHONE_NUM_PATTERN.matches(phoneNum)) {
            phoneNum
        } else {
            Constants.ISRAEL_PREFIX + phoneNum.drop(1)
        }
    }

    private fun getImageUrlResultListener(
        contactDetails: ContactDetails,
        contactIndex: Int = 0
    ): IRepositoryResult =
        object : IRepositoryResult {
            override fun <T> onSuccesses(data: T?) {
                if (data != null && data is Uri) {
                    contactDetails.imageUri = data.toString()
                    activeContactListFullListDetails.add(contactDetails)
                    activeFilterContactListDetails.add(contactDetails)
                    if (contactIndex == phoneNumToContacts.size) {
                        _isDoneLoadingContacts.value = true
                    }
                    viewModelScope.launch(Dispatchers.IO) {
                        contactsRepository.updateContactsDetails(contactDetails)
                    }
                }
            }

            override fun onFailed(message: String) {
                if (contactIndex == phoneNumToContacts.size) {
                    _isDoneLoadingContacts.value = true
                }
                Log.w(
                    TAG,
                    "Failed update image for Contacts: ${contactDetails.contactName} : $message"
                )
            }
        }
}