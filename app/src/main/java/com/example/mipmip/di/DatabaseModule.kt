package com.example.mipmip.di

import android.content.Context
import androidx.room.Room
import com.example.mipmip.dao.ContactDetailsDao
import com.example.mipmip.dao.MessageDao
import com.example.mipmip.database.AppDatabase
import com.example.mipmip.utils.Constants.MIP_MIP_DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)

class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, MIP_MIP_DB_NAME
        ).allowMainThreadQueries().
        fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideMessageDao(appDatabase: AppDatabase): MessageDao {
        return appDatabase.MessageDao()
    }

    @Singleton
    @Provides
    fun provideContactDao(appDatabase: AppDatabase): ContactDetailsDao {
        return appDatabase.contactDetailsDao()
    }
}