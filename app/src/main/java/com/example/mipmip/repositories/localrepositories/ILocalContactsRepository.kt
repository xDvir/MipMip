package com.example.mipmip.repositories.localrepositories

import com.example.mipmip.models.ContactDetails

interface ILocalContactsRepository {
    suspend fun getPhoneContacts(): List<ContactDetails>
    suspend fun getContactNumbers(): HashMap<String, ArrayList<String>>
    suspend fun updateContactsDetails(contactDetails: ContactDetails)
    suspend fun getAllLocalContacts(myPhoneNumber: String): List<ContactDetails>
    suspend fun getContactsDetailsLocally(contactPhoneNum: String): ContactDetails?
    suspend fun getContactNameByPhoneNumber(contactPhoneNumber: String): String?
}