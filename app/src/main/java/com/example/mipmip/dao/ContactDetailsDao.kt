package com.example.mipmip.dao

import androidx.room.*
import com.example.mipmip.models.ContactDetails
import com.example.mipmip.models.ContactImage
import com.example.mipmip.utils.Constants.CONTACTS_DETAILS_TABLE_NAME
import com.example.mipmip.utils.Constants.CONTACT_IMAGE_URL
import com.example.mipmip.utils.Constants.CONTACT_NAME
import com.example.mipmip.utils.Constants.CONTACT_PHONE_NUM

@Dao
interface ContactDetailsDao {

    @Query("SELECT $CONTACT_IMAGE_URL FROM $CONTACTS_DETAILS_TABLE_NAME WHERE $CONTACT_PHONE_NUM = :phoneNumber")
    suspend fun getContactImageUrl(phoneNumber: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateContactsDetails(contactDetails: ContactDetails)

    @Query("SELECT * FROM $CONTACTS_DETAILS_TABLE_NAME WHERE $CONTACT_PHONE_NUM != :myPhoneNumber and $CONTACT_PHONE_NUM != $CONTACT_NAME")
    suspend fun getAllLocalContacts(myPhoneNumber: String): List<ContactDetails>

    @Query("SELECT * FROM $CONTACTS_DETAILS_TABLE_NAME WHERE $CONTACT_PHONE_NUM = :contactPhoneNum")
    suspend fun getLocalContactsDetails(contactPhoneNum: String): ContactDetails?

}