package com.example.mipmip.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mipmip.utils.Constants


@Entity(tableName = Constants.CONTACTS_DETAILS_TABLE_NAME)
data class  ContactDetails(
    @PrimaryKey val phoneNum:String,
    @ColumnInfo(name = Constants.CONTACT_NAME) var contactName:String,
    val contactId:String,
    @ColumnInfo(name = Constants.CONTACT_IMAGE_URL) var imageUri: String? = "",
)