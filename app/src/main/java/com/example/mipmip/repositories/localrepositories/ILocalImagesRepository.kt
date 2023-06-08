package com.example.mipmip.repositories.localrepositories


interface ILocalImagesRepository {
    suspend fun insertLocalMyImageProfileUrl(phoneNum: String, imageUri: String)
    suspend fun getContactImageUrl(
        phoneNum: String,
    ): String?


}