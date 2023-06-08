package com.example.mipmip.repositories.repositoriesInterfaces

import android.net.Uri
import com.example.mipmip.repositories.IRepositoryResult

interface IImagesRepository {
    suspend fun getContactImageUrl(
        phoneNum: String,
        getImageUrlResultListener: IRepositoryResult
    )

    suspend fun uploadProfileImageAndContinue(
        phoneNUm: String,
        imageUri: Uri,
        uploadProfileImageResultListener: IRepositoryResult
    )

    suspend fun fetchRemoteImageProfile(
        phoneNum: String,
        getRemoteImageProfileResult: IRepositoryResult
    )

    suspend fun insertLocalMyImageProfileUrl(phoneNum: String, imageUri: String)
    suspend fun getImageUrlByPhoneNumber(contactPhoneNumber: String): Uri?
}