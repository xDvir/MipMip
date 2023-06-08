package com.example.mipmip.repositories.remoterepositories.remoteInterfaces

import android.net.Uri
import com.example.mipmip.repositories.IRepositoryResult

interface IRemoteImagesRepository {
    suspend fun uploadProfileImageAndContinue(
        phoneNum: String,
        imageUri: Uri,
        uploadProfileImageResultListener: IRepositoryResult
    )

    suspend fun fetchRemoteImageProfile(
        phoneNum: String,
        getRemoteImageProfileResult: IRepositoryResult
    )

    suspend fun getImageUrlByPhoneNumber(contactPhoneNumber: String): Uri?
}