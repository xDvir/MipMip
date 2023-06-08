package com.example.mipmip.repositories.repositoriesInterfaces

import android.app.Activity
import com.example.mipmip.repositories.IRepositoryResult
import java.lang.ref.WeakReference

interface IUsersRepository {
    fun getMyPhoneNum(): String?
    suspend fun createUser(
        phoneNum: String,
        loginActivityWR: WeakReference<Activity>,
        verifyPhoneNumResultListener: IRepositoryResult
    )

    suspend fun verifyOtp(otp: String, verifyOTPResultListener: IRepositoryResult)
    suspend fun isUserLoggedIn(isUserLoggedInListener: IRepositoryResult)
}