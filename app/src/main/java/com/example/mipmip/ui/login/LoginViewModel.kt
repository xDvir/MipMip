package com.example.mipmip.ui.login

import android.app.Activity
import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mipmip.ui.AuthenticationState
import com.example.mipmip.repositories.IRepositoryResult
import com.example.mipmip.repositories.ImagesRepository
import com.example.mipmip.repositories.UsersRepository
import com.example.mipmip.utils.Utils.Companion.addPrefixToPhoneNum
import com.example.mipmip.utils.Utils.Companion.phoneNumberIsValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val imagesRepository: ImagesRepository,
    application: Application
) : AndroidViewModel(application) {

    private var phoneNum: String? = null
    private var _remoteImageUriProfile = MutableStateFlow<Uri?>(null)
    private var _inputIsValid = MutableStateFlow(true)
    private var _progressIndicatorIsVisible = MutableStateFlow(false)
    private var _nextStepResult = MutableStateFlow("")
    private var _authenticationState = MutableStateFlow<AuthenticationState>(AuthenticationState.VerifyPhoneNum)
    val inputIsValid = _inputIsValid.asStateFlow()
    val nextStepResult = _nextStepResult.asStateFlow()
    val authenticationState = _authenticationState.asStateFlow()
    var progressIndicatorIsVisible = _progressIndicatorIsVisible.asStateFlow()
    var remoteImageUriProfile = _remoteImageUriProfile.asStateFlow()

    companion object {
        const val TAG = "LoginViewModel"
        const val INVALID_OTP_ERROR_MESSAGE = "OTP must be 4 digits"
    }


    private fun otpIsValid(otp: String): Boolean {
        return otp.length == 6
    }

    fun registerNewUser(phoneNum: String, loginActivityWR: WeakReference<Activity>) {
        if (phoneNumberIsValid(phoneNum)) {
            _inputIsValid.value = true
            _progressIndicatorIsVisible.value = true
            viewModelScope.launch {
                usersRepository.createUser(
                    addPrefixToPhoneNum(phoneNum),
                    loginActivityWR,
                    verifyPhoneNumResultListener
                )
            }
        } else {
            _inputIsValid.value = false
        }
    }

    fun verifyOTP(otp: String) {
        if (otpIsValid(otp)) {
            _progressIndicatorIsVisible.value = true
            viewModelScope.launch(Dispatchers.IO) {
                usersRepository.verifyOtp(otp, verifyOTPResultListener)
            }
        } else {
            _nextStepResult.value = INVALID_OTP_ERROR_MESSAGE
            _inputIsValid.value = false
        }
    }


    fun fetchRemoteImageProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            if (phoneNum != null) {
                imagesRepository.fetchRemoteImageProfile(phoneNum!!, fetchRemoteImageProfileResult)
            }
        }
    }

    fun uploadProfileImageAndContinue(imageUri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            _progressIndicatorIsVisible.value = true
            if (phoneNum != null) {
                if (imageUri != Uri.parse("") && imageUri !=null) {
                    imagesRepository.uploadProfileImageAndContinue(
                        phoneNum!!,
                        imageUri,
                        uploadProfileImageResultListener
                    )
                } else {
                    _nextStepResult.value = "please choose image"
                    _progressIndicatorIsVisible.value = false
                }
            } else {
                _progressIndicatorIsVisible.value = false
                _nextStepResult.value = "Please try again"
                _authenticationState.value = AuthenticationState.VerifyPhoneNum
            }
        }
    }

    fun saveLocalImageAndContinue(imageUri: Uri){
        viewModelScope.launch(Dispatchers.IO) {
            _progressIndicatorIsVisible.value = false
            _authenticationState.value = AuthenticationState.LoggedIn
            imagesRepository.insertLocalMyImageProfileUrl(phoneNum!!, imageUri.toString())
        }
    }

    private val verifyPhoneNumResultListener = object : IRepositoryResult {
        override fun <T> onSuccesses(data: T?) {
            _progressIndicatorIsVisible.value = false
            _inputIsValid.value = true
            _nextStepResult.value = ""
            _authenticationState.value = AuthenticationState.VerifyOtp

        }

        override fun onFailed(message: String) {
            _progressIndicatorIsVisible.value = false
            _nextStepResult.value = message
        }

    }

    private val verifyOTPResultListener = object : IRepositoryResult {
        override fun <T> onSuccesses(data: T?) {
            if (data != null) {
                phoneNum = data as String
                _progressIndicatorIsVisible.value = false
                _inputIsValid.value = true
                _nextStepResult.value = ""
                _authenticationState.value = AuthenticationState.EditProfile
                fetchRemoteImageProfile()
            }
        }

        override fun onFailed(message: String) {
            _progressIndicatorIsVisible.value = false
            _nextStepResult.value = message
            _inputIsValid.value = false
        }
    }

    private val fetchRemoteImageProfileResult = object : IRepositoryResult {
        override fun <T> onSuccesses(data: T?) {
            if (data != null) {
                _remoteImageUriProfile.value = data as Uri
            }
        }

        override fun onFailed(message: String) {
            _progressIndicatorIsVisible.value = false
            _remoteImageUriProfile.value = Uri.parse("")
            Log.e(TAG, "Error while fetching image profile $message")
        }

    }

    private val uploadProfileImageResultListener = object : IRepositoryResult {
        override fun <T> onSuccesses(data: T?) {
            viewModelScope.launch(Dispatchers.IO) {
                if (phoneNum != null && data != null) {
                    imagesRepository.insertLocalMyImageProfileUrl(phoneNum!!, data.toString())
                }
                _progressIndicatorIsVisible.value = false
                _authenticationState.value = AuthenticationState.LoggedIn
            }
        }

        override fun onFailed(message: String) {
            _progressIndicatorIsVisible.value = false
            _nextStepResult.value = "Please try again later.."
            Log.e(TAG, "Error while update profile image $message")
        }
    }
}