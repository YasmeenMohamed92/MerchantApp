package com.example.merchantapp.presentation.login.validation

import javax.inject.Inject

class LoginValidator @Inject constructor() {

    fun validateForm(
        merchantId: String,
        pin: String
    ): Boolean {
        return merchantId.length == 14 &&
                merchantId.all { it.isDigit() } &&
                pin.length == 6 &&
                pin.all { it.isDigit() }
    }

    fun isValidPinInput(value: String): Boolean {
        return value.length <= 6 &&
                value.all { it.isDigit() }
    }
}