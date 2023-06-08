package com.example.mipmip.repositories

import android.app.Activity
import com.example.mipmip.repositories.IRepositoryResult
import com.example.mipmip.repositories.repositoriesInterfaces.IUsersRepository
import com.example.mipmip.repositories.remoterepositories.remoteInterfaces.IRemoteUsersRepository
import java.lang.ref.WeakReference
import javax.inject.Inject

class UsersRepository @Inject constructor(
    private val remoteUsersRepository: IRemoteUsersRepository,
) : IUsersRepository {

    override fun getMyPhoneNum(): String? {
        return remoteUsersRepository.getMyPhoneNum()
    }

    override suspend fun createUser(
        phoneNum: String,
        loginActivityWR: WeakReference<Activity>,
        verifyPhoneNumResultListener: IRepositoryResult
    ) {
        remoteUsersRepository.createUser(phoneNum, loginActivityWR, verifyPhoneNumResultListener)
    }

    override suspend fun verifyOtp(otp: String, verifyOTPResultListener: IRepositoryResult) {
        remoteUsersRepository.verifyOtp(otp, verifyOTPResultListener)
    }

    override suspend fun isUserLoggedIn(isUserLoggedInListener: IRepositoryResult) {
        remoteUsersRepository.isUserLoggedIn(isUserLoggedInListener)
    }
}