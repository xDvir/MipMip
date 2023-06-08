package com.example.mipmip.repositories

import com.example.mipmip.models.ContactDetails
import com.example.mipmip.repositories.repositoriesInterfaces.IContactsRepository
import com.example.mipmip.repositories.localrepositories.ILocalContactsRepository
import com.example.mipmip.repositories.remoterepositories.remoteInterfaces.IRemoteContactsRepository
import javax.inject.Inject

class ContactsRepository @Inject constructor(
    private val remoteContactsRepository: IRemoteContactsRepository,
    private val localContactRepository: ILocalContactsRepository
): IContactsRepository {

    override suspend fun getPhoneContacts(): List<ContactDetails> {
        return localContactRepository.getPhoneContacts()
    }

    override suspend fun getContactNumbers(): HashMap<String, ArrayList<String>> {
        return localContactRepository.getContactNumbers()
    }

    override suspend fun updateContactsDetails(contactDetails: ContactDetails) {
        localContactRepository.updateContactsDetails(contactDetails)
    }

    override suspend fun getAllLocalContacts(myPhoneNumber: String): List<ContactDetails> {
        return localContactRepository.getAllLocalContacts(myPhoneNumber)
    }

    override suspend fun getContactsDetailsLocally(
        contactPhoneNum: String,
    ): ContactDetails? {
        return localContactRepository.getContactsDetailsLocally(contactPhoneNum)
    }

    override suspend fun getContactNameByPhoneNumber(contactPhoneNum: String): String {
        return localContactRepository.getContactNameByPhoneNumber(contactPhoneNum)
            ?: return contactPhoneNum
    }
}
