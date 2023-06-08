package com.example.mipmip.models


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.mipmip.utils.Constants
import com.example.mipmip.utils.Constants.MESSAGES_TABLE_NAME
import com.example.mipmip.utils.Constants.MESSAGE_ID
import com.example.mipmip.utils.Constants.MESSAGE_READ
import com.example.mipmip.utils.Constants.MESSAGE_RECIPIENT
import com.example.mipmip.utils.Constants.MESSAGE_SENDER
import com.example.mipmip.utils.Constants.MESSAGE_TEXT
import com.example.mipmip.utils.Constants.MESSAGE_TIME
import com.google.firebase.database.PropertyName
import java.util.*

@Entity(tableName = MESSAGES_TABLE_NAME)
data class Message(

    @get:PropertyName(MESSAGE_ID)
    @ColumnInfo(name = MESSAGE_ID)
    @PrimaryKey(autoGenerate = false)
    var id: String = "",
    @get:PropertyName(MESSAGE_TEXT)
    @ColumnInfo(name = MESSAGE_TEXT)
    val text: String,
    @get:PropertyName(MESSAGE_TIME)
    @ColumnInfo(name = MESSAGE_TIME)
    val time: Long,
    @get:PropertyName(MESSAGE_SENDER)
    @ColumnInfo(name = MESSAGE_SENDER)
    val sender: String,
    @get:PropertyName(MESSAGE_RECIPIENT)
    @ColumnInfo(name = MESSAGE_RECIPIENT)
    val recipient: String,
    @get:PropertyName(Constants.MESSAGE_CONTACT_PHONE_NUM)
    @ColumnInfo(name = Constants.MESSAGE_CONTACT_PHONE_NUM)
    var messageContactPhoneNum: String,
    @get:PropertyName(MESSAGE_READ)
    @ColumnInfo(name = MESSAGE_READ)
    var isRead: Boolean = false
)

//class Converters {
//    @TypeConverter
//    fun fromTimestamp(value: Long): Date {
//        return Date(value)
//    }
//
//    @TypeConverter
//    fun dateToTimestamp(date: Date): Long {
//        return date.time
//    }
//}