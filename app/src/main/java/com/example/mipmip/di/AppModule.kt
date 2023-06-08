package com.example.mipmip.di

import android.content.Context
import com.example.mipmip.dao.ContactDetailsDao
import com.example.mipmip.dao.MessageDao
import com.example.mipmip.repositories.localrepositories.*
import com.example.mipmip.repositories.ImagesRepository
import com.example.mipmip.repositories.remoterepositories.remoteInterfaces.IRemoteContactsRepository
import com.example.mipmip.repositories.remoterepositories.remoteInterfaces.IRemoteUsersRepository
import com.example.mipmip.repositories.remoterepositories.firebaseRemoteRepositories.FirebaseRemoteContactsRepository
import com.example.mipmip.repositories.remoterepositories.firebaseRemoteRepositories.FirebaseRemoteImagesRepository
import com.example.mipmip.repositories.remoterepositories.firebaseRemoteRepositories.FirebaseRemoteUsersRepository
import com.example.mipmip.repositories.remoterepositories.remoteInterfaces.IRemoteImagesRepository
import com.example.mipmip.repositories.remoterepositories.remoteInterfaces.IRemoteMessagesRepository
import com.example.mipmip.repositories.remoterepositories.firebaseRemoteRepositories.FirebaseRemoteMessagesRepository
import com.example.mipmip.repositories.ContactsRepository
import com.example.mipmip.repositories.MessagesRepository
import com.example.mipmip.repositories.repositoriesInterfaces.IMessagesRepository
import com.example.mipmip.repositories.UsersRepository
import com.example.mipmip.repositories.repositoriesInterfaces.IContactsRepository
import com.example.mipmip.repositories.repositoriesInterfaces.IImagesRepository
import com.example.mipmip.repositories.repositoriesInterfaces.IUsersRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)

class AppModule {

    @Singleton
    @Provides
    fun provideUsersRepository(remoteUsersRepository: IRemoteUsersRepository): IUsersRepository {
        return UsersRepository(remoteUsersRepository)
    }

    @Singleton
    @Provides
    fun provideRemoteUsersRepository(@ApplicationContext context: Context): IRemoteUsersRepository {
        return FirebaseRemoteUsersRepository(context)
    }

    @Singleton
    @Provides
    fun provideContactsRepository(remoteContactsRepository: IRemoteContactsRepository,localContactsRepository: ILocalContactsRepository): IContactsRepository {
        return ContactsRepository(remoteContactsRepository,localContactsRepository)
    }

    @Singleton
    @Provides
    fun provideMessagesRepository(remoteMessagesRepository: IRemoteMessagesRepository,localMessagesRepository: ILocalMessagesRepository): IMessagesRepository {
        return MessagesRepository(remoteMessagesRepository,localMessagesRepository)
    }

    @Singleton
    @Provides
    fun provideRemoteMessagesRepository(): IRemoteMessagesRepository {
        return FirebaseRemoteMessagesRepository()
    }

    @Singleton
    @Provides
    fun provideLocalMessagesRepository(messageDao: MessageDao):ILocalMessagesRepository{
        return LocalMessagesRepository(messageDao)
    }

    @Singleton
    @Provides
    fun provideRemoteContactsRepository(@ApplicationContext context: Context): IRemoteContactsRepository {
        return FirebaseRemoteContactsRepository(context)
    }

    @Singleton
    @Provides
    fun provideLocalContactsRepository(@ApplicationContext context: Context,contactDetailsDao: ContactDetailsDao): ILocalContactsRepository {
        return LocalContactsRepository(context,contactDetailsDao)
    }

    @Singleton
    @Provides
    fun provideImagesRepository(remoteImagesRepository: IRemoteImagesRepository,localImagesRepository: LocalImagesRepository): IImagesRepository {
        return ImagesRepository(remoteImagesRepository,localImagesRepository)
    }

    @Singleton
    @Provides
    fun provideRemoteImagesRepository(): IRemoteImagesRepository {
        return FirebaseRemoteImagesRepository()
    }

    @Singleton
    @Provides
    fun provideLocalImagesRepository(contactDetailsDao: ContactDetailsDao): ILocalImagesRepository {
        return LocalImagesRepository(contactDetailsDao)
    }



}