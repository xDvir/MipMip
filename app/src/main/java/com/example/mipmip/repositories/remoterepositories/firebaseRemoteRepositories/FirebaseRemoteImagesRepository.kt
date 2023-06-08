package com.example.mipmip.repositories.remoterepositories.firebaseRemoteRepositories

import android.content.Context
import android.net.Uri
import com.example.mipmip.repositories.IRepositoryResult
import com.example.mipmip.repositories.remoterepositories.remoteInterfaces.IRemoteImagesRepository
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRemoteImagesRepository @Inject constructor() : IRemoteImagesRepository {

    private val storageRef: FirebaseStorage = FirebaseStorage.getInstance()
    private val profileImagesStorage = storageRef.reference.child(PROFILE_IMAGES_STORAGE)

    companion object {
        const val TAG = "FirebaseRemoteImagesRepository"
        const val PROFILE_IMAGES_STORAGE = "profile_images"
        const val PROFILE_IMAGE = "profile_image.jpg"
        const val FILE_DOES_NOT_EXISTS =  "File does not exist"
    }

    override suspend fun uploadProfileImageAndContinue(
        phoneNum: String,
        imageUri: Uri,
        uploadProfileImageResultListener: IRepositoryResult
    ) {
        val profileImageRef = profileImagesStorage.child("/$phoneNum/$PROFILE_IMAGE")
        profileImageRef.putFile(imageUri)
            .addOnSuccessListener {
                profileImageRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        uploadProfileImageResultListener.onSuccesses(uri)
                    }.addOnFailureListener { e ->
                        uploadProfileImageResultListener.onFailed("Failed to get download URL: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                uploadProfileImageResultListener.onFailed("Failed to upload image: ${e.message}")
            }
    }

    override suspend fun fetchRemoteImageProfile(
        phoneNum: String,
        getRemoteImageProfileResult: IRepositoryResult
    ) {
        profileImagesStorage.child("/$phoneNum/$PROFILE_IMAGE").downloadUrl.addOnSuccessListener { uri ->
            getRemoteImageProfileResult.onSuccesses(uri)
        }.addOnFailureListener { error ->
            if (error is StorageException && error.errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                getRemoteImageProfileResult.onFailed(FILE_DOES_NOT_EXISTS)
            } else {
                error.message?.let {
                    getRemoteImageProfileResult.onFailed(it)
                }
            }
        }
    }

    override suspend fun getImageUrlByPhoneNumber(contactPhoneNumber: String): Uri? {
        return try {
            profileImagesStorage.child("/$contactPhoneNumber/$PROFILE_IMAGE").downloadUrl.await()
        } catch (e: Exception) {
            null
        }
    }
}
