package com.example.mipmip.repositories.repositoriesInterfaces

import com.example.mipmip.models.ContactDetails
import com.example.mipmip.models.Message
import com.example.mipmip.repositories.IRepositoryResult

interface IContactsRepository {
    suspend fun getPhoneContacts(): List<ContactDetails>
    suspend fun getContactNumbers(): HashMap<String, ArrayList<String>>
    suspend fun updateContactsDetails(contactDetails: ContactDetails)
    suspend fun getAllLocalContacts(myPhoneNumber:String):List<ContactDetails>
    suspend fun getContactsDetailsLocally(contactPhoneNum: String): ContactDetails?
    suspend fun getContactNameByPhoneNumber(contactPhoneNum: String): String
}
