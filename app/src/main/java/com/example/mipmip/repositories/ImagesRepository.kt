package com.example.mipmip.repositories

import android.net.Uri
import androidx.core.net.toUri
import com.example.mipmip.repositories.repositoriesInterfaces.IImagesRepository
import com.example.mipmip.repositories.IRepositoryResult
import com.example.mipmip.repositories.localrepositories.ILocalImagesRepository
import com.example.mipmip.repositories.remoterepositories.remoteInterfaces.IRemoteImagesRepository

import javax.inject.Inject

class ImagesRepository @Inject constructor(
    private val remoteImagesRepository: IRemoteImagesRepository,
    private val localImagesRepository: ILocalImagesRepository,
) : IImagesRepository {

    override suspend fun insertLocalMyImageProfileUrl(phoneNum: String, imageUri: String) {
        localImagesRepository.insertLocalMyImageProfileUrl(phoneNum, imageUri)
    }

    override suspend fun getContactImageUrl(
        phoneNum: String,
        getImageUrlResultListener: IRepositoryResult
    ) {
        val contactImageUrl = localImagesRepository.getContactImageUrl(phoneNum)
        if (contactImageUrl == null) {
            remoteImagesRepository.fetchRemoteImageProfile(phoneNum, getImageUrlResultListener)
        } else {
            getImageUrlResultListener.onSuccesses(contactImageUrl.toUri())
        }
    }

    override suspend fun uploadProfileImageAndContinue(
        phoneNUm: String,
        imageUri: Uri,
        uploadProfileImageResultListener: IRepositoryResult
    ) {
        remoteImagesRepository.uploadProfileImageAndContinue(
            phoneNUm,
            imageUri,
            uploadProfileImageResultListener
        )
    }

    override suspend fun fetchRemoteImageProfile(
        phoneNum: String,
        getRemoteImageProfileResult: IRepositoryResult
    ) {
        remoteImagesRepository.fetchRemoteImageProfile(phoneNum, getRemoteImageProfileResult)
    }

    override suspend fun getImageUrlByPhoneNumber(contactPhoneNumber: String): Uri? {
        return remoteImagesRepository.getImageUrlByPhoneNumber(contactPhoneNumber)
    }
}