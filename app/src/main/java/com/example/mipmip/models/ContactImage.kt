package com.example.mipmip.models


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mipmip.utils.Constants
import com.example.mipmip.utils.Constants.CONTACTS_DETAILS_TABLE_NAME

@Entity(tableName = CONTACTS_DETAILS_TABLE_NAME)
data class ContactImage(
    @PrimaryKey val phoneNum:String,
    @ColumnInfo(name = Constants.CONTACT_IMAGE_URL) val imageUrl: String,
)
