package com.example.mipmip.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mipmip.dao.ContactDetailsDao
import com.example.mipmip.dao.MessageDao
import com.example.mipmip.models.ContactDetails
import com.example.mipmip.models.Message

@Database(entities = [ContactDetails::class,Message::class], version = 1,exportSchema = false)
//@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDetailsDao(): ContactDetailsDao
    abstract fun MessageDao(): MessageDao
}