package com.example.mipmip.repositories.localrepositories

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import com.example.mipmip.dao.ContactDetailsDao
import com.example.mipmip.models.ContactDetails
import com.example.mipmip.utils.Constants
import com.example.mipmip.utils.Constants.EMPTY_STRING
import com.example.mipmip.utils.Utils.Companion.removePrefixToPhoneNum
import javax.inject.Inject

class LocalContactsRepository @Inject constructor(context: Context,private val contactDetailsDao: ContactDetailsDao) : ILocalContactsRepository {

    private val mContext = context

    companion object {
        const val ASC = "ASC"
    }

    override suspend fun getContactNumbers(): HashMap<String, ArrayList<String>> {
        val contactsNumberMap = HashMap<String, ArrayList<String>>()
        val phoneCursor: Cursor? = mContext.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        if (phoneCursor != null && phoneCursor.count > 0) {
            val contactIdIndex =
                phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val numberIndex =
                phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (phoneCursor.moveToNext()) {
                val contactId = phoneCursor.getString(contactIdIndex)
                val number: String = phoneCursor.getString(numberIndex)
                    .replace(Regex(Constants.PATTERN_REMOVE_SPACES_AND_DASHES), EMPTY_STRING)
                //check if the map contains key or not, if not then create a new array list with number
                if (contactsNumberMap.containsKey(contactId)) {
                    contactsNumberMap[contactId]?.add(number)
                } else {
                    contactsNumberMap[contactId] = arrayListOf(number)
                }
            }
            phoneCursor.close()
        }
        return contactsNumberMap
    }

    override suspend fun getPhoneContacts(): List<ContactDetails> {

        val contactsList = ArrayList<ContactDetails>()
        val contactsCursor = mContext.contentResolver?.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " " + ASC
        )
        if (contactsCursor != null && contactsCursor.count > 0) {
            val idIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            while (contactsCursor.moveToNext()) {
                val id = contactsCursor.getString(idIndex)
                val contactName = contactsCursor.getString(nameIndex)
                if (contactName != null) {
                    contactsList.add(
                        ContactDetails("", contactName, id)
                    )
                }
            }
            contactsCursor.close()
        }
        return contactsList
    }


    override suspend fun getContactNameByPhoneNumber(contactPhoneNumber: String): String? {
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val selection = "${ContactsContract.CommonDataKinds.Phone.NUMBER} = ? OR ${ContactsContract.CommonDataKinds.Phone.NUMBER} = ?"
        val selectionArgs = arrayOf(contactPhoneNumber, removePrefixToPhoneNum(contactPhoneNumber))

        val cursor = mContext.contentResolver?.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        var contactName: String? = null

        if (cursor != null && cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            if (nameIndex > -1) {
                contactName = cursor.getString(nameIndex)
            }
        }
        cursor?.close()
        return contactName
    }

    override suspend fun updateContactsDetails(contactDetails: ContactDetails) {
        contactDetailsDao.updateContactsDetails(contactDetails)
    }

    override suspend fun getAllLocalContacts(myPhoneNumber: String): List<ContactDetails> {
        return contactDetailsDao.getAllLocalContacts(myPhoneNumber)
    }

    override suspend fun getContactsDetailsLocally(contactPhoneNum: String): ContactDetails? {
        return contactDetailsDao.getLocalContactsDetails(contactPhoneNum)
    }



}
