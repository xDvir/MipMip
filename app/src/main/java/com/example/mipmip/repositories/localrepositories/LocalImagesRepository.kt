package com.example.mipmip.repositories.localrepositories

import android.net.Uri
import com.example.mipmip.dao.ContactDetailsDao
import com.example.mipmip.models.ContactDetails
import com.example.mipmip.models.ContactImage
import com.example.mipmip.utils.Constants.EMPTY_STRING
import javax.inject.Inject

class LocalImagesRepository @Inject constructor(
    private val contactDetailsDao: ContactDetailsDao) :
    ILocalImagesRepository {

    override suspend fun getContactImageUrl(phoneNum: String): String? {
        return contactDetailsDao.getContactImageUrl(phoneNum)
    }

    override suspend fun insertLocalMyImageProfileUrl(phoneNum: String, imageUri: String) {
        val contactDetails = ContactDetails(phoneNum,EMPTY_STRING,"-1", imageUri)
        contactDetailsDao.updateContactsDetails(contactDetails)
    }

}