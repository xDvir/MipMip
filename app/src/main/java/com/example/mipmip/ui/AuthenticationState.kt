package com.example.mipmip.ui

sealed class AuthenticationState{
    object VerifyPhoneNum: AuthenticationState()
    object VerifyOtp: AuthenticationState()
    object EditProfile: AuthenticationState()
    object LoggedIn: AuthenticationState()
    object NotLoggedIn : AuthenticationState()
    object Loading : AuthenticationState()
}
