package com.example.merchantapp.presentation.login.validation

import javax.inject.Inject

class LoginValidator @Inject constructor() {
    fun validateForm(merchantId: String, pin: String): Boolean {
        return merchantId.isNotBlank() && pin.length == 6
    }

    fun isValidPinInput(value: String): Boolean {
        return value.all { it.isDigit() }
    }
}