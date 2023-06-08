package com.example.mipmip.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mipmip.models.Message
import com.example.mipmip.utils.Constants.MESSAGES_TABLE_NAME
import com.example.mipmip.utils.Constants.MESSAGE_CONTACT_PHONE_NUM
import com.example.mipmip.utils.Constants.MESSAGE_RECIPIENT
import com.example.mipmip.utils.Constants.MESSAGE_SENDER
import com.example.mipmip.utils.Constants.MESSAGE_TIME

@Dao
interface MessageDao {
    @Query("SELECT * FROM $MESSAGES_TABLE_NAME WHERE $MESSAGE_SENDER = :phoneNumber OR $MESSAGE_RECIPIENT = :phoneNumber ORDER BY $MESSAGE_TIME DESC")
    suspend fun getLastLocalMessage(phoneNumber: String): MutableList<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNewMessage(message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessagesList(messages: List<Message>)

    @Query(
        """ SELECT * FROM $MESSAGES_TABLE_NAME WHERE ($MESSAGE_CONTACT_PHONE_NUM, $MESSAGE_TIME) IN (
            SELECT $MESSAGE_CONTACT_PHONE_NUM, MAX($MESSAGE_TIME) FROM $MESSAGES_TABLE_NAME
            GROUP BY $MESSAGE_CONTACT_PHONE_NUM) ORDER BY $MESSAGE_TIME DESC"""
    )
    suspend fun getLatestMessagesForEachContact(): MutableList<Message>
}