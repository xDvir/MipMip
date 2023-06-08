package com.example.mipmip.utils

import com.example.mipmip.ui.login.LoginViewModel
import com.example.mipmip.utils.Constants.ISRAEL_PREFIX

class  Utils {
    companion object {
        fun phoneNumberIsValid(phoneNum: String): Boolean {
            val pattern = """^05\d{8}$""".toRegex()
            return pattern.matches(phoneNum)
        }

         fun addPrefixToPhoneNum(phoneNum: String): String {
            return ISRAEL_PREFIX + phoneNum.drop(1)
        }

        fun removePrefixToPhoneNum(phoneNum: String):String{
            return "0" + phoneNum.drop(ISRAEL_PREFIX.length)
        }
    }
}