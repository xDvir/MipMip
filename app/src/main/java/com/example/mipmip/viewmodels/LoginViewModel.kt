package com.example.mipmip.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class LoginViewModel : ViewModel() {

    private var _phoneNumText = MutableStateFlow("")
    private var _phoneNumTextIsValid = MutableStateFlow(true)
    val phoneNumText = _phoneNumText
    val phoneNumTextIsValid = _phoneNumTextIsValid

    private fun isValidPhoneNumber(): Boolean {
        return _phoneNumText.value.length == 10 && _phoneNumText.value.startsWith("05")
    }

    fun onPhoneTextChanged(numText: String) {
        _phoneNumText.value = numText
    }

    fun registerNewUser(){
        if(!isValidPhoneNumber()) {
            _phoneNumTextIsValid.value=false
        }
        else {
            _phoneNumTextIsValid.value=true
        }
    }

}