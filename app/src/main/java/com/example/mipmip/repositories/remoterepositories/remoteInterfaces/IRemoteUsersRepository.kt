package com.example.mipmip.repositories.remoterepositories.remoteInterfaces

import android.app.Activity
import com.example.mipmip.models.ContactDetails
import com.example.mipmip.repositories.IListenerAction
import com.example.mipmip.repositories.IRepositoryResult
import java.lang.ref.WeakReference


interface IRemoteUsersRepository {

    suspend fun createUser(
        phoneNum: String,
        loginActivityWR: WeakReference<Activity>,
        verifyPhoneNumResultListener: IRepositoryResult
    )

    suspend fun verifyOtp(otp: String, verifyOTPResultListener: IRepositoryResult)
    suspend fun setStatusChangedAction(phoneNum: String, isActive: IListenerAction<Boolean>)
    suspend fun getActiveContactsList(verifyPhoneNumContactsList: List<ContactDetails>): List<ContactDetails>
    suspend fun isUserLoggedIn(isUserLoggedInListener: IRepositoryResult)
    fun getMyPhoneNum(): String?
    fun sendOtp(phoneNum: String, loginActivityWR: WeakReference<Activity>)
}
